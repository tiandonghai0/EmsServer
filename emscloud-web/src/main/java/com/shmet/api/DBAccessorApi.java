package com.shmet.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.bean.SQLReadyWarpper;

@CrossOrigin
@Controller
public class DBAccessorApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SQLManager sqlManager;

    public ModelAndView login(HttpServletRequest request) {
        String redirect_url = request.getParameter("redirect_url");
        Map<String, Object> map = new HashMap<>();
        map.put("redirect_url", redirect_url);
        return new ModelAndView("/api/dbaccessor.btl", map);
    }

    public String select(@RequestBody SQLReadyWarpper sqlReady,
                         HttpServletRequest request,
                         HttpServletResponse response) throws JsonProcessingException {
        String rst = null;

        List<Map> rstMap = sqlManager.execute(sqlReady.getSQLReadyInstance(), Map.class);
        if (rstMap != null) {
            ObjectMapper om = new ObjectMapper();
            rst = om.writeValueAsString(rstMap);
        }
        return rst;
    }

    public int update(@RequestBody SQLReadyWarpper sqlReady,
                      HttpServletRequest request,
                      HttpServletResponse response) throws JsonProcessingException {
        return sqlManager.executeUpdate(sqlReady.getSQLReadyInstance());
    }
}
