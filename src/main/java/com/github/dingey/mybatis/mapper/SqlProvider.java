package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * mybatis通用mapper接口
 */
@SuppressWarnings("all")
public class SqlProvider {
	private static final HashMap<String, String> sqls = new HashMap<>();
	private static final HashMap<String, List<Field>> modelFieldsMap = new HashMap<>();
	private static final HashMap<Class<?>, Field> idFieldsMap = new HashMap<>();

	private SqlProvider() {
	}

	/**
	 * 获取insertSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String insert(Object bean) {
		return cachedSql(bean, "insert", t -> getInsertSql(bean, false));
	}

	/**
	 * 获取insertSQL，忽略null列
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String insertSelective(Object bean) {
		return getInsertSql(bean, true);
	}

	/**
	 * 获取insertSQL
	 *
	 * @param bean      模型对象
	 * @param selective 忽略null
	 * @return SQL
	 */
	public static String getInsertSql(Object bean, boolean selective) {
		StringBuilder sql = new StringBuilder();
		List<String> props = new ArrayList<>();
		List<String> columns = new ArrayList<>();
		sql.append("insert into ").append(JPA.table(bean)).append("(");
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (!JPA.insertable(field)) {
					continue;
				}
				columns.add(StringUtil.snakeCase(field.getName()));
				props.add("#{" + field.getName() + "}");
			}
		} catch (Exception e) {
			throw new MapperException(sql.toString(), e);
		}
		for (int i = 0; i < columns.size(); i++) {
			sql.append(columns.get(i));
			if (i != columns.size() - 1)
				sql.append(",");
		}
		sql.append(")").append(" values(");
		for (int i = 0; i < props.size(); i++) {
			sql.append(props.get(i));
			if (i != props.size() - 1)
				sql.append(",");
		}
		sql.append(")");
		return sql.toString();
	}

	/**
	 * 获取updateSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String update(Object bean) {
		return cachedSql(bean, "update", t -> getUpdateSql(bean, false));
	}

	/**
	 * 获取updateSQL,忽略null
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String updateSelective(Object bean) {
		return getUpdateSql(bean, true);
	}

	/**
	 * 获取updateSQL
	 *
	 * @param bean      模型对象
	 * @param selective 忽略null
	 * @return SQL
	 */
	public static String getUpdateSql(Object bean, boolean selective) {
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(JPA.table(bean)).append(" set ");
		String id = null;
		String version = null;
		try {
			for (Field field : getCachedModelFields(bean.getClass())) {
				if (selective) {
					Object value = field.get(bean);
					if (value == null) {
						continue;
					}
				}
				if (field.isAnnotationPresent(Id.class)) {
					id = field.getName();
					continue;
				} else if (field.isAnnotationPresent(Version.class)) {
					version = field.getName();
					continue;
				} else if (!JPA.updatable(field)) {
					continue;
				}
				sql.append(StringUtil.snakeCase(field.getName())).append("=#{").append(field.getName()).append("},");
			}
		} catch (Exception e) {
			throw new MapperException(sql.toString(), e);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(Const.WHERE);
		sql.append(StringUtil.snakeCase(id)).append(" =#{").append(id).append(Const.AND1);
		if (version != null) {
			sql.append(Const.AND).append(StringUtil.snakeCase(version)).append("=#{").append(version).append("} and");
		}
		return sql.delete(sql.length() - 4, sql.length()).toString();
	}

	/**
	 * 获取deleteSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String delete(Object bean) {
		return cachedSql(bean, "delete", t -> {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from ").append(JPA.table(bean)).append(Const.WHERE);
			List<String> ids = new ArrayList<>();
			try {
				for (Field field : getCachedModelFields(bean.getClass())) {
					if (field.isAnnotationPresent(Id.class)) {
						ids.add(field.getName());
					}
				}
			} catch (Exception e) {
				throw new MapperException(sql.toString(), e);
			}
			if (ids.isEmpty()) {
				throw new MapperException("ids不能为空");
			} else {
				for (String id : ids) {
					sql.append(StringUtil.snakeCase(id)).append("=#{").append(id).append(Const.AND1);
				}
			}
			return sql.delete(sql.length() - 5, sql.length()).toString();
		});
	}

	/**
	 * 获取deleteSQL
	 *
	 * @param context context
	 * @return SQL
	 */
	public static String deleteMark(ProviderContext context) {
		Class<?> entity = MapperMethod.entity(context);
		return getCachedSql(entity, "deleteMark", t -> {
			StringBuilder sql = new StringBuilder();
			sql.append("update ").append(JPA.table(entity)).append(" set ");
			String delete = null;
			String version = null;
			List<String> ids = new ArrayList<>();
			try {
				for (Field field : getCachedModelFields(entity)) {
					if (field.isAnnotationPresent(DeleteMark.class)) {
						delete = StringUtil.snakeCase(field.getName());
					} else if (field.isAnnotationPresent(Id.class)) {
						ids.add(field.getName());
					} else if (field.isAnnotationPresent(Version.class)) {
						version = field.getName();
					}
				}
			} catch (Exception e) {
				throw new MapperException(sql.toString(), e);
			}
			sql.append(delete).append("=1 where ");
			if (ids.isEmpty()) {
				throw new MapperException("主键必须声明");
			} else {
				for (String id : ids) {
					sql.append(StringUtil.snakeCase(id)).append("=#{").append(id).append(Const.AND1);
				}
				if (version != null) {
					sql.append(StringUtil.snakeCase(version)).append("=#{").append(version).append(Const.AND1);
				}
			}
			return sql.delete(sql.length() - 5, sql.length()).toString();
		});
	}

	/**
	 * 获取selectSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String get(Object bean) {
		return cachedSql(bean, "get", t -> {
			StringBuilder sql = new StringBuilder();
			sql.append(Const.SELECT_FROM).append(JPA.table(bean)).append(Const.WHERE);
			try {
				for (Field f : getCachedModelFields(bean.getClass())) {
					if (f.isAnnotationPresent(Id.class)) {
						sql.append(StringUtil.snakeCase(f.getName())).append("=#{").append(f.getName()).append("} and");
					}
				}
				sql.delete(sql.toString().length() - 3, sql.toString().length());
			} catch (Exception e) {
				throw new MapperException(sql.toString(), e);
			}
			return sql.toString();
		});
	}

	/**
	 * 获取selectSQL
	 *
	 * @param context context
	 * @return SQL
	 */
	public static String getById(ProviderContext context) {
		Class<?> entity = MapperMethod.entity(context);
		return getCachedSql(entity, "getById", t -> {
			StringBuilder sql = new StringBuilder();
			sql.append(Const.SELECT_FROM).append(JPA.table(entity)).append(Const.WHERE);
			try {
				for (Field field : getCachedModelFields(entity)) {
					if (field.isAnnotationPresent(Id.class)) {
						sql.append(StringUtil.snakeCase(field.getName())).append("=#{param1}");
						break;
					}
				}
			} catch (Exception e) {
				throw new MapperException(sql.toString(), e);
			}
			return sql.toString();
		});
	}

	/**
	 * 获取selectSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String list(Object bean) {
		StringBuilder sql = new StringBuilder();
		sql.append(Const.SELECT_FROM).append(JPA.table(bean)).append(" where 1=1 ");
		String orderby = null;
		try {
			for (Field f : getCachedModelFields(bean.getClass())) {
				if (f.get(bean) != null && !f.isAnnotationPresent(Transient.class)) {
					if (f.isAnnotationPresent(OrderBy.class)) {
						orderby = String.valueOf(f.get(bean));
					} else {
						sql.append(Const.AND).append(StringUtil.snakeCase(f.getName())).append("=#{").append(f.getName()).append("}");
					}
				}
			}
		} catch (Exception e) {
			throw new MapperException(sql.toString(), e);
		}
		if (orderby != null) {
			if (orderby.contains("order by")) {
				sql.append(orderby);
			} else {
				sql.append(" order by ").append(orderby);
			}
		}
		return sql.toString();
	}

	/**
	 * 获取selectcountSQL
	 *
	 * @param bean 模型对象
	 * @return SQL
	 */
	public static String count(Object bean) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(0) from ").append(JPA.table(bean)).append(" where 1=1 ");
		try {
			for (Field f : getCachedModelFields(bean.getClass())) {
				if (f.get(bean) != null && !f.isAnnotationPresent(Transient.class))
					sql.append(Const.AND).append(StringUtil.snakeCase(f.getName())).append("=#{").append(f.getName()).append("}");
			}
		} catch (Exception e) {
			throw new MapperException(sql.toString(), e);
		}
		return sql.toString();
	}

	/**
	 * 获取selectSQL
	 *
	 * @param context context
	 * @return SQL
	 */
	public static String listAll(ProviderContext context) {
		Class<?> entity = MapperMethod.entity(context);
		return getCachedSql(entity, "listAll", t -> Const.SELECT_FROM + JPA.table(entity));
	}

	/**
	 * 获取selectSQL
	 *
	 * @param context context
	 * @return SQL
	 */
	public static String countAll(ProviderContext context) {
		Class<?> entity = MapperMethod.entity(context);
		return getCachedSql(entity, "countAll", t -> "select count(0) from " + JPA.table(entity));
	}

	/**
	 * 获取selectSQL
	 *
	 * @param ids 主键
	 * @return SQL
	 */
	public static String listByIds(Iterable<Serializable> ids, ProviderContext context) {
		StringBuilder s = new StringBuilder();
		Class<?> entity = MapperMethod.entity(context);
		s.append(Const.SELECT_FROM).append(JPA.table(entity)).append(Const.WHERE);
		s.append(StringUtil.snakeCase(id(entity).getName())).append(" in ( ");
		for (Serializable id : ids) {
			s.append("'").append(id).append("',");
		}
		s.deleteCharAt(s.length() - 1).append(" )");
		return s.toString();
	}

	/**
	 * 获取主键field
	 *
	 * @param entity 模型对象
	 * @return SQL
	 */
	public static Field id(Class<?> entity) {
		Field id = null;
		if (idFieldsMap.containsKey(entity)) {
			id = idFieldsMap.get(entity);
		} else {
			for (Field f : getCachedModelFields(entity)) {
				if (f.isAnnotationPresent(Id.class)) {
					if (!f.isAccessible())
						f.setAccessible(true);
					id = f;
					idFieldsMap.put(entity, f);
					break;
				}
			}
		}
		if (id == null)
			throw new MapperException(entity.getName() + "没有主键!");
		return id;
	}

	/**
	 * 获取缓存的sql
	 *
	 * @param bean   对象实例
	 * @param method 方法
	 * @param func   func
	 * @return SQL
	 */
	private static String cachedSql(Object bean, String method, Func<Object> func) {
		String key = bean.getClass().getName() + "_" + method;
		if (sqls.get(key) != null) {
			return sqls.get(key);
		} else {
			String res = func.apply(bean);
			sqls.put(key, res);
			return res;
		}
	}

	/**
	 * 获取缓存的sql
	 *
	 * @param bean   对象类
	 * @param method 方法
	 * @param func   func
	 * @return SQL
	 */
	private static String getCachedSql(Class<?> bean, String method, Func<Class<?>> func) {
		String k = bean.getName() + "_" + method;
		if (sqls.containsKey(k)) {
			return sqls.get(k);
		} else {
			String apply = func.apply(bean);
			sqls.put(k, apply);
			return apply;
		}
	}

	/**
	 * 获取缓存fields
	 *
	 * @param beanClass 对象类
	 * @return SQL
	 */
	private static List<Field> getCachedModelFields(Class<?> beanClass) {
		if (modelFieldsMap.containsKey(beanClass.getName())) {
			return modelFieldsMap.get(beanClass.getName());
		} else {
			List<Field> fields = ClassUtil.getDeclaredFields(beanClass);
			fields.forEach(f -> f.setAccessible(true));
			modelFieldsMap.put(beanClass.getName(), fields);
			return fields;
		}
	}
}
