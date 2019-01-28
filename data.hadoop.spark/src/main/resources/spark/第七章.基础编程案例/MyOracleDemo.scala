package mydemo

import java.sql.DriverManager

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

/*
JdbcRDD参数的说明
class JdbcRDD[T](
sc : org.apache.spark.SparkContext,                     SparkContext对象
getConnection : scala.Function0[java.sql.Connection],   数据库的链接
sql : scala.Predef.String,                              SQL语句   ---> 只能是select
lowerBound : scala.Long,                                SQL的第一个参数
upperBound : scala.Long,                                SQL的第二个参数
numPartitions : scala.Int,                              分区的个数
mapRow : scala.Function1[java.sql.ResultSet, T]         查询的结果集

JdbcRDD的缺点：
1、SQL语句   ---> 只能是select
2、lowerBound和upperBound: 说明select 必须有两个参数，类型：Long
 */

object MyOracleDemo {
  //创建变量代表数据库的链接
  val connection = () =>{
    //注册驱动
    Class.forName("oracle.jdbc.OracleDriver").newInstance()
    //获取链接
    DriverManager.getConnection("jdbc:oracle:thin:@192.168.88.101:1521/orcl.example.com","scott","tiger")
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","D:\\vidoes")

    //创建spacrkconf对象
    val conf = new SparkConf().setAppName("My WebCount Demo").setMaster("local")

    //创建sparkcontext
    val sc = new SparkContext(conf)

    //创建一个JdbcRDD
    val oracleRDD = new JdbcRDD(sc,connection,"select * from emp where deptno=? and sal>?",10,2000,1,rs=>{
      //处理结果集: 得到员工的姓名和薪水
      val ename = rs.getString("ename")
      val sal = rs.getInt("sal")
      (ename,sal)
    })

    //执行
    val result = oracleRDD.collect()
    println(result.toBuffer)

    sc.stop()
  }
}

















