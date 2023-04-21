package com.shmet;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.gen.GenConfig;

public class GenEntitySQLAll {
    public static void main(String[] args) throws Throwable {
        ConnectionSource source = ConnectionSourceHelper.getSimple("com.zaxxer.hikari.HikariDataSource", "jdbc:mysql://www.jz-energy.cn:3306/ems?autoReconnect=true&failOverReadOnly=false", "root", "!qaz2wsx");
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql/gen");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

        //GenConfig.initTemplate("/com/shmet/mypojo.btl");
        GenConfig config = new GenConfig();
        config.preferBigDecimal(true);
        sqlManager.genALL("com.shmet.entity.mysql.gen", config, null);

    }


}
