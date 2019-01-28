package com.zhangfang.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SparkJunit implements Serializable {
    public static final String HADOOPURI = "hdfs://localhost:9000";
    private SparkConf sparkConf;
    private JavaSparkContext javaSparkContext;

    @Before
    public void before() {
        sparkConf = new SparkConf().setAppName("Spark").setMaster("local");
        javaSparkContext = new JavaSparkContext(sparkConf);
    }

    @Test
    public void run001() {
        //构造集合
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        //并行化集合，创建初始RDD
        JavaRDD<Integer> numberRDD = javaSparkContext.parallelize(numbers,3);
        //使用map算子，将集合中的每个元素都乘以2
        JavaRDD<Integer> multipleNumberRDD = numberRDD.map((Function<Integer, Integer>) v1 -> v1 * 2);
        //打印新的RDD
        multipleNumberRDD.foreach((VoidFunction<Integer>) t -> System.out.println(t));
    }

    @Test
    public void run002() {
        JavaRDD<String> tradeRDD = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaPairRDD<String, Integer> result = tradeRDD
                .filter((String s) -> {
                    Boolean flag = false;
                    String[] strArr = s.split(",");
                    if (strArr[1].equals("zhangfang"))
                        flag = true;
                    return flag;
                })
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
    }

    @After
    public void after() {
        javaSparkContext.close();
    }
}
