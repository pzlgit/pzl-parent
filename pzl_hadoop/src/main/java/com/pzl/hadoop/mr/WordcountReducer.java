package com.pzl.hadoop.mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author pzl
 * @version 1.0
 * @date 2020-07-13
 */
public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    int sum;
    IntWritable v = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        sum = 0;
        for (IntWritable value : values) {
            sum += value.get();
        }
        v.set(sum);
        context.write(key, v);
    }

}