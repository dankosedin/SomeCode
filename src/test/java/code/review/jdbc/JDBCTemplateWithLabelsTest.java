package code.review.jdbc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class JDBCTemplateWithLabelsTest {
    @Mock
    private JdbcTemplate template;

    @Mock
    private PreparedStatement ps;

    private JDBCTemplateWithLabels my_Template;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        my_Template = new JDBCTemplateWithLabels(template,
                "select user_id, user_name from user where user_id > (userId)");
    }

    @Test
    public void test() throws SQLException {
        given(template.query(anyString(), any(PreparedStatementSetter.class), any(ResultSetExtractor.class))).willReturn(null);

        Object answer = my_Template.query(new HashMap<String, Object>() {{
            put("userId", 4);
        }}, rs -> null);

        Assert.assertEquals("should return null", null, answer);

        ArgumentCaptor<PreparedStatementSetter> pssSetter = ArgumentCaptor.forClass(PreparedStatementSetter.class);
        verify(template, times(1)).query(anyString(), pssSetter.capture(), any(ResultSetExtractor.class));

        PreparedStatementSetter value = pssSetter.getValue();
        value.setValues(ps);
        verify(ps, times(1)).setObject(anyInt(), anyObject());
    }

}