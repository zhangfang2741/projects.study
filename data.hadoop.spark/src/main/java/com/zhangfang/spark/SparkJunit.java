package com.zhangfang.spark;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SparkJunit implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String HADOOPURI = "hdfs://localhost:9000";
    private SparkConf sparkConf;
    private JavaSparkContext javaSparkContext;

    @Before
    public void before() {
        sparkConf = new SparkConf().setAppName("Spark").setMaster("local");
        javaSparkContext = new JavaSparkContext(sparkConf);
    }

    /**
     * Map
     */
    @Test
    public void run_Map() {
        //构造集合
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        //并行化集合，创建初始RDD
        JavaRDD<Integer> numberRDD = javaSparkContext.parallelize(numbers, 3);
        //使用map算子，将集合中的每个元素都乘以2
        JavaRDD<Integer> multipleNumberRDD = numberRDD.map((Function<Integer, Integer>) v1 -> v1 * 2);
        //multipleNumberRDD.collect();
        //打印新的RDD
        multipleNumberRDD.foreach((VoidFunction<Integer>) t -> System.out.println(t));
    }

    /**
     * Filter
     */
    @Test
    public void run_Filter() {
        JavaRDD<String> tradeRDD = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaPairRDD<String, Integer> result = tradeRDD
                .filter((Function<String, Boolean>) s -> {
                    Boolean flag = false;
                    String[] strArr = s.split(",");
                    if (strArr[1].equals("zhangfang"))
                        flag = true;
                    return flag;
                })
                .mapToPair((PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        result.foreach((VoidFunction<Tuple2<String, Integer>>) t -> System.out.println(t._2));

    }

    /**
     * FlatMap
     */
    @Test
    public void run_FlatMap() {
        JavaRDD<String> tradeRDD = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaRDD<String> results = tradeRDD.flatMap((FlatMapFunction<String, String>) s -> {
            //return Arrays.asList(s).iterator();
            return new ArrayListIterator(s.split(""));
        });
        results.foreach((VoidFunction<String>) t -> System.out.println(t));
    }

    /**
     * Union
     */
    @Test
    public void run_Union() {
        JavaRDD<String> tradeRDD1 = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaRDD<String> tradeRDD2 = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaRDD<String> tradeRDD3 = tradeRDD1.union(tradeRDD2);
        tradeRDD3.foreach((VoidFunction<String>) t -> System.out.println(t));
    }

    /**
     * Intersection
     */
    @Test
    public void run_Intersection() {
        JavaRDD<String> tradeRDD1 = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaRDD<String> tradeRDD2 = javaSparkContext.textFile(HADOOPURI + "/zhangfang/trade.txt");//.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        JavaRDD<String> tradeRDD3 = tradeRDD1.intersection(tradeRDD2);
        tradeRDD3.foreach((VoidFunction<String>) t -> System.out.println(t));
    }

    /**
     * Distinct
     */
    @Test
    public void run_Distinct() {
        JavaRDD<Integer> numRDD = javaSparkContext.parallelize(Arrays.asList(1, 2, 3, 3, 3, 3, 3, 3, 4, 5));
        JavaRDD<Integer> distinctRDD = numRDD.distinct();
        //按RDD中每个元素的第二部分进行排序
        JavaRDD<Integer> sortByRDD = distinctRDD.sortBy((Function<Integer, Object>) v1 -> v1, false, 1);
        sortByRDD.foreach((VoidFunction<Integer>) t -> System.out.println(t));
        System.out.println(sortByRDD.top(3));

    }

    /**
     * GroupbyKey
     */
    @Test
    public void run_GroupbyKey() {
//        List<Integer> data = Arrays.asList(1, 2, 4,4,4, 3, 5, 6, 7);
//        JavaRDD<Integer> javaRDD = javaSparkContext.parallelize(data);
//        //转为k，v格式
//        JavaPairRDD<Integer, String> javaPairRDD = javaRDD.mapToPair((PairFunction<Integer, Integer, String>) i -> new Tuple2<>(i, UUID.randomUUID().toString()));
//        JavaPairRDD<Integer, Iterable<String>> groupByKeyRDD = javaPairRDD.groupByKey();
//        System.out.println(groupByKeyRDD.collect());
        List<String> sentencesList = Arrays.asList("I Love China", "I Love USA", "I Love The World");
        JavaRDD<String> javaRDD = javaSparkContext.parallelize(sentencesList);
        JavaRDD<String> flatJavaRDD = javaRDD.flatMap((FlatMapFunction<String, String>) els -> new ArrayListIterator(els.split(" ")));
        JavaPairRDD<String, Integer> javaPairRDD = flatJavaRDD.mapToPair((PairFunction<String, String, Integer>) els -> new Tuple2<>(els, 1));
        /**
         *  reduceByKey方法，类似于MR的reduce
         *  要求被操作的数据（即下面实例中的javaPairRDD）是KV键值对形式，该方法会按照key相同的进行聚合，在两两运算
         */
        JavaPairRDD<String, Integer> counts = javaPairRDD.reduceByKey((Function2<Integer, Integer, Integer>) (i1, i2) -> i1 + i2);
        System.out.println(counts.collect());
    }

    /**
     * ReducebyKey
     */
    @Test
    public void run_ReducebyKey() {
//        List<String> sentencesList=Arrays.asList("I Love China","I Love USA","I Love The World");
//        JavaRDD<String> javaRDD=javaSparkContext.parallelize(sentencesList);
//        JavaRDD<String> flatJavaRDD=javaRDD.flatMap(( FlatMapFunction<String,String>) els->new ArrayListIterator(els.split(" ")));
//        JavaPairRDD<String, Integer> javaPairRDD=flatJavaRDD.mapToPair((PairFunction<String,String,Integer>) els-> new Tuple2<>(els,1));
//        JavaPairRDD<String, Iterable<Integer>> reduceByKeyRDD = javaPairRDD.reduceByKey();
//        System.out.println(reduceByKeyRDD.collect());
    }

    /**
     * 笛卡儿积
     */
    @Test
    public void run_Cartesian() {
        JavaRDD rdd1 = javaSparkContext.parallelize(Arrays.asList("Tom", "Bob"));
        JavaRDD rdd2 = javaSparkContext.parallelize(Arrays.asList("Tom", "Marry", "Alice"));
        JavaPairRDD rdd3 = rdd1.cartesian(rdd2);
        System.out.println(rdd3.collect());
        System.out.println(rdd3.count());

    }
    /**
     * reduce
     */
    @Test
    public void run_Reduce() {
        JavaRDD<Integer> javaRDD=javaSparkContext.parallelize(Arrays.asList(1,2,3,4,5,5,5,6,7));
        Integer reduceRDD=javaRDD.reduce((Function2<Integer,Integer,Integer>)(v1, v2)-> v1+v2);
        System.out.println(reduceRDD);
    }
    @After
    public void after() {
        javaSparkContext.close();
    }
}
