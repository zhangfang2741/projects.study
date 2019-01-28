package mydemo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

//ʹ��Java���Կ���Spark����WordCount
/*
 * ����: hdfs://hadoop111:9000/data/data.txt
 * I love Beijing
 * I love China
 * Beijing is the capital of China
 */
public class JavaWordCount {
	
	//����ģʽ��ʹ�ÿո���зִ�
	private static final Pattern SPACE = Pattern.compile(" ");

	public static void main(String[] args) {
		//����SparkConf
		SparkConf conf = new SparkConf().setAppName("My Java WordCount");
		
		//���� SparkContext����: JavaSparkContext
		JavaSparkContext ctx = new JavaSparkContext(conf);
		
		//��HDFS�϶�ȡ����
		JavaRDD<String>  lines = ctx.textFile(args[0]);
		
		//ѹƽ���������ҷִ�: ����ÿһ������
		JavaRDD<String>  words = lines.flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterator<String> call(String s) throws Exception {
				// s: ������������: I love Beijing
				return Arrays.asList(SPACE.split(s)).iterator();
			}
		});
		
		//��ÿһ����������һ��Ԫ�飺(I,1)
		//k2��v2����Map����Ľ��
		JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(String s) throws Exception {
				// s�����ʣ� Beijing ----> (Beijing,1)
				return new Tuple2<String, Integer>(s,1);
			}
		});
		
		//reduce����reduceByKey(_+_)
		JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
			
			@Override
			public Integer call(Integer i1, Integer i2) throws Exception {
				//��i1��i2�ۻ����
				return i1+i2;
			}
		});
	
		//��������õ����Ľ��������action����
		List<Tuple2<String, Integer>>  result = counts.collect();
		
		//������: ֱ�Ӵ�ӡ����Ļ��
		for(Tuple2<String, Integer> t:result){
			System.out.println(t._1()+":"+t._2());
		}
		
		//ֹͣcontext����
		ctx.stop();
	}
}














