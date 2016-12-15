package code.review.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDBCTemplateWithLabels {

    private JdbcTemplate template;

    private String sql;

    public JDBCTemplateWithLabels(JdbcTemplate template, String sql) {
        this.template = template;
        this.sql = sql;
    }

    public <T> T query(Map<String, ?> labels, ResultSetExtractor<T> rse)
            throws DataAccessException {
        return template.query(createSql(sql, labels), createSetter(sql, labels), rse);
    }

    private PreparedStatementSetter createSetter(String sql, Map<String, ?> paramMap) {
        Map<String, Integer> indexes = new HashMap<>();
        Matcher matcher = Pattern.compile("\\(\\w*\\)").matcher(sql);
        int index = 0;
        while (matcher.find()) {
            indexes.put(matcher.group(), index++);
        }
        return ps -> {
            for (String key : paramMap.keySet()) {
                ps.setObject(indexes.get("(" + key + ")"), paramMap.get(key));
            }
        };
    }

    private String createSql(String sql, Map<String, ?> paramMap) {
        return sql.replaceAll("\\(\\w*\\)", "?");
    }
}
