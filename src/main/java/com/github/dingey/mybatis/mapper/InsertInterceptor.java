package com.github.dingey.mybatis.mapper;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import javax.persistence.SequenceGenerator;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author d
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class InsertInterceptor implements Interceptor {
    private static final Log log = LogFactory.getLog(InsertInterceptor.class);
    private static final HashMap<Class<?>, String> SEQUENCE = new HashMap<>();

    public InsertInterceptor() {
    }

    public InsertInterceptor(MapperProperties properties) {
        Const.camelCase = properties.isCamelCase();
        Const.columnUpper = properties.isColumnUpper();
        Const.tablePrefix = properties.getTablePrefix();
        Const.tableUpper = properties.isTableUpper();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (isSequence(invocation)) {
            generateId(invocation.getArgs()[1], (Executor) invocation.getTarget());
        }
        return invocation.proceed();
    }

    private boolean isSequence(Invocation invocation) {
        return SqlCommandType.INSERT.equals(((MappedStatement) invocation.getArgs()[0]).getSqlCommandType())
                && ((invocation.getArgs()[1]).getClass().isAnnotationPresent(SequenceGenerator.class)
                || SqlProvider.id((invocation.getArgs()[1]).getClass()).isAnnotationPresent(SequenceGenerator.class));
    }

    private void generateId(Object parameter, Executor executor) throws Throwable {
        Field idField = SqlProvider.id(parameter.getClass());
        String seq;
        if (!SEQUENCE.containsKey(parameter.getClass())) {
            SequenceGenerator sequenceGenerator = parameter.getClass().getAnnotation(SequenceGenerator.class);
            if (sequenceGenerator == null) {
                sequenceGenerator = SqlProvider.id(parameter.getClass()).getAnnotation(SequenceGenerator.class);
            }
            seq = String.format("select %s.nextval from dual", sequenceGenerator.sequenceName().isEmpty() ? sequenceGenerator.name() : sequenceGenerator.sequenceName());
            SEQUENCE.put(parameter.getClass(), seq);
        } else {
            seq = SEQUENCE.get(parameter.getClass());
        }
        PreparedStatement preparedStatement = executor.getTransaction().getConnection().prepareStatement(seq);
        ResultSet resultSet = preparedStatement.executeQuery();
        Object id = null;
        if (resultSet.next()) {
            if (idField.getType() == int.class || idField.getType() == Integer.class) {
                id = resultSet.getInt(1);
            } else if (idField.getType() == long.class || idField.getType() == Long.class) {
                id = resultSet.getLong(1);
            } else if (idField.getType() == String.class) {
                id = resultSet.getString(1);
            } else {
                id = resultSet.getObject(1);
            }
            idField.set(parameter, id);
        }
        close(resultSet, preparedStatement);
        if (log.isDebugEnabled()) {
            log.debug("==>  Preparing: " + seq);
            log.debug(" <==      Return: " + id);
        }
    }

    private void close(ResultSet resultSet, Statement statement) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
