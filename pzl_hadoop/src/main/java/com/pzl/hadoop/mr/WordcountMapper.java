package com.pzl.hadoop.mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * WordcountMapper
 *
 * @author pzl
 * @version 1.0
 * @date 2020-07-13
 */
public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    Text k = new Text();
    IntWritable v = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        System.out.println(key + "-" + value);
        String line = value.toString();
        String[] fields = line.split(" ");
        for (String word : fields) {
            k.set(word);
            context.write(k, v);
        }
    }
}