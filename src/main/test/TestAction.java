import com.mysql.jdbc.PreparedStatement;
import com.opt.model.Page;
import com.opt.model.Student;
import com.opt.service.impl.StudentServiceImpl;
import com.opt.util.MemcacheUtil;
import com.opt.util.RandomStudent;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TestAction {

    static Logger logger = Logger.getLogger(TestAction.class);

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationcontext.xml");

        MemCachedClient memCachedClient = (MemCachedClient) applicationContext.getBean("memcachedClient");

        memCachedClient.set("test2","im value2");

        logger.info(memCachedClient.get("test2"));


    }


    @Test
    public void jedisTest(){

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationcontext.xml");
        JedisPool jedisPool = (JedisPool) applicationContext.getBean("jedisPool");
        StudentServiceImpl studentService = (StudentServiceImpl) applicationContext.getBean("studentService");
//        ShardedJedisPool shardedJedisPool = (ShardedJedisPool) applicationContext.getBean("shardedJedisPool");
//        ShardedJedis shardedJedis = shardedJedisPool.getResource();

        Page<Student> page = studentService.findByPage(1,5);
        Jedis jedis = jedisPool.getResource();
//        jedis.set("zhang1","张强");
        jedis.set("page1", String.valueOf(page));
        System.out.println(jedis.get("page1"));

    }



    @Test
    public void insertCrud(){

        String url = "jdbc:mysql://47.98.50.21/jnshu?characterEncoding=UTF-8";

        String user = "root";
        String pwd = "zhangqiang";
        StringBuffer sql = new StringBuffer("insert into jnshu_students (stuName,stuPhoto,sex,age,school,office,recommend,pro_id) values ");
        RandomStudent ranStudent = new RandomStudent();

        Connection connection = null;
        PreparedStatement pstm = null;


        try {
            connection = DriverManager.getConnection(url,user,pwd);

            for (int i=0;i<100;i++){
                if(i>0)sql.append(",");
                sql.append("('" + ranStudent.getNameBuilder().toString() + "'," +
                        "'/stat/images/7-task8.png'," +
                        "1," +
                        ranStudent.getAge(15,40) + ",'" +
                        ranStudent.getSchool() + "','" +
                        ranStudent.getPro() + "','" +
                        ranStudent.getPro() + "'," +
                        "1)"
                );
            }

            pstm = (PreparedStatement) connection.prepareStatement(sql.toString());

            pstm.executeUpdate();
//            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (connection != null) connection.close();
                if (pstm != null) pstm.close();
            }catch (SQLException e) {
                    e.printStackTrace();
            }
        }

    }




}
