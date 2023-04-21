package com.shmet.api;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shmet.dao.mongodb.FirstRecordDataDao;
import com.shmet.entity.mongo.FirstRecordData;

@CrossOrigin
@Controller
public class RealDataApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    FirstRecordDataDao firstRecordDataDao;

    @ResponseBody
    @RequestMapping("/firstrecord/all")
    public List<FirstRecordData> getAllFirstRecord(HttpServletRequest request) {
        return firstRecordDataDao.findAllData();
    }
}
