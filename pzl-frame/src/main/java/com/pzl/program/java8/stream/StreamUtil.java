package com.pzl.program.java8.stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pzl.program.toolkit.convert.BeanConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 数据流
 * <p>
 * 一、java8出的新特性中最重要的一个特征。
 * Stream是Java 8新增的重要特性, 它提供函数式编程支持并允许以管道方式操作集合.
 * 流操作会遍历数据源, 使用管道式操作处理数据后生成结果集合, 这个过程通常不会对数据源造成影响.
 * 1、Stream 自己不会存储元素。
 * 2、Stream 不会改变源对象。相反， 他们会返回一个持有结果的新Stream。
 * 3、Stream 操作是延迟执行的。这意味着 他们会等到需要结果的时候才执行。
 * <p>
 * 二、使用步骤
 * 1、创建一个Stream：一个数据源（数组、集合）
 * 2、中间操作：一个中间操作，处理数据源数据
 * 3、终止操作：一个终止操作，执行中间操作链，产生结果
 * <p>
 * 三、流的创建
 * 1、可以使用集合类的stream()或者parallelStream()方法创建流:
 * Stream<String> s1 = Arrays.asList("a","b","c").stream();
 * Stream<String> s2 = Arrays.asList("a","b","c").parallelStream();
 * 注：对比发现parallelStream执行效率要比传统的for循环和stream要快的多，
 * 那么什么时候要用stream或者parallelStream呢？可以从以下三点入手考虑
 * <p>
 * 是否需要并行？  
 * 任务之间是否是独立的？是否会引起任何竞态条件？  
 * 结果是否取决于任务的调用顺序？
 * <p>
 * 2、Stream的静态方法of()也可以用来创建流:
 * Stream.of(new int[]{1,2,3});
 * 3、Arrays也提供了创建流的静态方法stream():
 * Arrays.stream(new int[]{1,2,3})
 * 4、一些类也提供了创建流的方法:
 * IntStream.range(start, stop);
 * BufferedReader.lines();
 * Random.ints();
 * <p>
 * 四、中间操作
 * 流操作是惰性执行的, 中间操作会返回一个新的流对象, 当执行终点操作时才会真正进行计算.介绍流的中间操作.
 * 除非传入的操作函数有副作用, 函数本身不会对数据源进行任何修改.
 * -----------------------------1、distinct-----------------------------------------------------------------------------
 * distinct保证数据源中的重复元素在结果中只出现一次, 它使用equals()方法判断两个元素是否相等.
 * List<String> list = Stream.of("a","b","c","b").distinct().collect(Collectors.toList());
 * -----------------------------2、filter-------------------------------------------------------------------------------
 * filter根据传入的断言函数对所有元素进行检查, 只有使断言函数返回真的元素才会出现在结果中. filter不会对数据源进行修改.
 * List<Integer> list = IntStream.range(1,10).boxed().filter( i -> i % 2 == 0).collect(Collectors.toList());
 * <p>
 * java.util.Objects提供了空元素过滤的工具:
 * List<MyItem> list = items.stream().filter(Objects::nonNull).collect(Collectors.toList());
 * -------------------------------3、map--------------------------------------------------------------------------------
 * map方法根据传入的mapper函数对元素进行一对一映射, 即数据源中的每一个元素都会在结果中被替换(映射)为mapper函数的返回值.
 * List<String> list = Stream.of('a','b','c').map( s -> s.hashCode()).collect(Collectors.toList());
 * --------------------------------4、flatMap---------------------------------------------------------------------------
 * 与map不同flatMap进行多对一映射, 它要求若数据源的元素类型为R, 则mapper函数的返回值必须为Stream<R>.
 * flatMap会使用mapper函数将数据源中的元素一一映射为Stream对象, 然后把这些Stream拼装成一个流.
 * 因此我们可以使用flatMap进行合并列表之类的操作:
 * List<Integer> list = Stream.of(Arrays.asList(1),Arrays.asList(2, 3),Arrays.asList(4, 5, 6))
 * .flatMap(l -> l.stream()).collect(Collectors.toList());
 * ---------------------------------5、peek-----------------------------------------------------------------------------
 * peek方法会对数据源中所有元素进行给定操作, 但在结果中仍然是数据源中的元素. 通常我们利用操作的副作用, 修改其它数据或进行输入输出.
 * List<String> list = Stream.of('a','b','c').map(s -> System.out.println(s)).collect(Collectors.toList());
 * ---------------------------------6、sorted----------------------------------------------------------------------------
 * sorted方法用于对数据源进行排序:
 * List<Integer> list = Arrays.asList(1,2,3,4,5,6).stream().sorted((a, b) -> a-b).collect(Collectors.toList());
 * 使用java.util.Comparator是更方便的方法, 默认进行升序排序:
 * class Item {
 * int val;
 * public Item(int val) { this.val = val; }
 * public int getVal() { return val; }
 * }
 * <p>
 * List<Item> list = Stream.of(
 * new Item(1),
 * new Item(2),
 * new Item(3)
 * )
 * .sorted(Comparator.comparingInt(Item::getVal)).collect(Collectors.toList());
 * 使用reversed()方法进行降序排序:
 * List<Item> list = Stream.of(
 * new Item(1),
 * new Item(2),
 * new Item(3)
 * )
 * .sorted(Comparator.comparingInt(Item::getVal).reversed()).collect(Collectors.toList());
 * ----------------------------------7、limit---------------------------------------------------------------------------
 * limit(int n)当流中元素数大于n时丢弃超出的元素, 否则不进行处理, 达到限制流长度的目的.
 * <p>
 * ---------------------------------8、skip-----------------------------------------------------------------------------
 * skip(int)返回丢弃了前n个元素的流. 如果流中的元素小于或者等于n，则返回空的流
 * <p>
 * 五、终点操作
 * ---------------------------------------------1、collect---------------------------------------------------------------
 * collect是使用最广泛的终点操作, 也上文中多次出现:
 * List<String> list = Stream.of("a","b","c","b").distinct().collect(Collectors.toList())
 * toList()将流转换为List实例, 是最常见的用法, java.util.Collectors类中还有求和, 计算均值, 取最值, 字符串连接等多种收集方法.
 * -----------------------------------------------2、forEach-------------------------------------------------------------
 * forEach方法对流中所有元素执行给定操作, 没有返回值.
 * Stream.of(1,2,3,4,5).forEach(System.out::println);
 * 其它：count() 返回流中的元素数；toArray(): 转换为数组
 * <p>
 * 六、并发问题
 * 除非显式地创建并行流, 否则默认创建的都是串行流.Collection.stream()为集合创建串行流,而Collection.parallelStream()创建并行流.
 * stream.parallel()方法可以将串行流转换成并行流,stream.sequential()方法将流转换成串行流.
 * 流可以在非线程安全的集合上创建, 流操作不应该对非线程安全的数据源产生任何副作用, 否则将发生java.util.ConcurrentModificationException异常.
 * <p>
 * List<String> list = new ArrayList(Arrays.asList("a", "b"));
 * list = list.stream().forEach(s -> l.add("c")); // cause exception
 * 对于线程安全的容器不会存在这个问题:
 * List<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));
 * list = list.stream().forEach(s -> l.add("c")); // safe operation
 * 当然建议Stream操作不要对数据源进行任何修改. 当然, 修改其它数据或者输入输出是允许的:
 * list.stream().forEach(s -> {set.add(s);System.out.println(s);});
 * 理想的管道操作应该是无状态且与访问顺序无关的. 无状态是指操作的结果只与输入有关, 下面即是一个有状态的操作示例:
 * <p>
 * State state = getState();
 * List<String> list = new ArrayList(Arrays.asList("a", "b"));
 * list = list.stream().map(s -> {
 * if (state.isReady()) {
 * return s;
 * }
 * else {
 * return null;
 * }
 * });
 * 无状态的操作保证无论系统状态如何管道的行为不变, 与顺序无关则有利于进行并行计算.
 *
 * @author pzl
 * @date 2020-04-05
 */
@Slf4j
public class StreamUtil {

    /**
     * 数字集合数据排序(升序)
     *
     * @param col 数据集合
     * @return 排序后的数据集合
     */
    public Collection<Double> sortAsc(Collection<Double> col) {
        List<Double> collect = col.stream().sorted(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return Double.compare(o1, o2);
            }
        }).collect(Collectors.toList());
        log.info("数据集合排序后{}", collect);
        return collect;
    }

    /**
     * 创建流对象
     *
     * @param col 数据集合
     * @return stream
     */
    public Stream<T> createStream(Collection<T> col) {
        Stream<T> stream = col.stream();
        //Stream<Collection<T>> stream1 = Stream.of(col);
        return stream;
    }

    /**
     * 创建流对象(管道并行，任务的调用顺序不一。)
     *
     * @param col 数据集合
     * @return stream
     */
    public Stream<T> createParallelStream(Collection<T> col) {
        Stream<T> stream = col.parallelStream();
        return stream;
    }

    /**
     * distinct保证数据源中的重复元素在结果中只出现一次, 它使用equals()方法判断两个元素是否相等.
     *
     * @param col 数据集合
     * @return 去重后数据
     */
    public Collection<T> distinct(Collection<T> col) {
        Stream<T> stream = createStream(col);
        List<T> collect = stream.distinct().collect(Collectors.toList());
        return collect;
    }

    /**
     * 过滤空元素
     *
     * @param col 数据集合
     * @return 过滤空元素后数据
     */
    public Collection<T> filterNull(Collection<T> col) {
        Stream<T> stream = createStream(col);
        List<T> list = stream.filter(Objects::nonNull).collect(Collectors.toList());
        return list;
    }

    /**
     * map 对元素进行一对一映射
     *
     * @param col 数据集合
     * @return 映射后的数据集合
     */
    public Collection<T> map(Collection<T> col) {
        Stream<T> stream = createStream(col);
        List list = stream.map(s -> s.hashCode()).collect(Collectors.toList());
        return list;
    }

    /**
     * 合并集合列表
     *
     * @param col1 数据集合1
     * @param col2 数据集合2
     * @return 合并后数据集合
     */
    public Collection<T> flatMap(Collection<T> col1, Collection<T> col2) {
        List<T> collect = Stream.of(col1, col2).flatMap(l -> l.stream()).collect(Collectors.toList());
        return collect;
    }

    /**
     * 通过key去重
     *
     * @param keyExtractor function
     * @param <T>          t
     * @return data
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * List通过Map映射成新List列表
     *
     * @param srcList 原始List
     * @param mapper  元素映射器
     * @param <T>     t
     * @param <R>     r
     * @return data
     */
    public static <T, R> List<R> listMap2List(List<T> srcList, Function<? super T, ? extends R> mapper) {
        if (isEmpty(srcList)) {
            return getEmptyList();
        }
        return srcList.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * List通过Map映射成Set列表
     *
     * @param srcList 原始list
     * @param mapper  元素映射器
     * @param <T>     t
     * @param <R>     r
     * @return data
     */
    public static <T, R> Set<R> listMap2Set(List<T> srcList, Function<? super T, ? extends R> mapper) {
        if (isEmpty(srcList)) {
            return getEmptySet();
        }
        return srcList.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * 适用于value为List的Map，将List元素类型进行转换
     *
     * @param srcMap      原始map
     * @param tarEleClass list元素需映射为哪个类型
     * @param <K>         k
     * @param <T>         t
     * @param <R>         r
     * @return data
     */
    public static <K, T, R> Map<K, List<R>> mapValueListEleConvert(Map<K, List<T>> srcMap, Class<R> tarEleClass) {
        if (isEmpty(srcMap)) {
            return getEmptyMap();
        }
        // 将列表内元素类型转换
        Map<K, List<R>> retMap = new HashMap<>(srcMap.size());
        srcMap.entrySet().forEach(entry -> {
            retMap.put(entry.getKey(), listMap2List(entry.getValue(), tag -> BeanConvertUtil.convert(tag, tarEleClass)));
        });
        return retMap;
    }

    /**
     * List分组
     *
     * @param srcList   原始list
     * @param keyMapper 映射器：将List元素映射成Map的key
     * @param <T>       t
     * @param <R>       r
     * @return data
     */
    public static <T, R> Map<R, List<T>> listGroupingBy(List<T> srcList, Function<? super T, ? extends R> keyMapper) {
        if (isEmpty(srcList)) {
            return getEmptyMap();
        }
        return srcList.stream().collect(Collectors.groupingBy(keyMapper));
    }

    /**
     * List转Map
     *
     * @param srcList 原始List
     * @param mapper  映射器：将List元素映射成Map的key
     * @param <T>     t
     * @param <R>     r
     * @return data
     */
    public static <T, R> Map<R, T> list2Map(List<T> srcList, Function<? super T, ? extends R> mapper) {
        if (isEmpty(srcList)) {
            return getEmptyMap();
        }
        return srcList.stream().collect(Collectors.toMap(mapper, v -> v));
    }

    /**
     * List转Map，保持原始顺序
     *
     * @param srcList 原始List
     * @param mapper  映射器：将List元素映射成Map的key
     * @param <T>     t
     * @param <R>     r
     * @return data
     */
    public static <T, R> Map<R, T> list2MapKeepOrder(List<T> srcList, Function<? super T, ? extends R> mapper) {
        if (isEmpty(srcList)) {
            return getEmptyMap();
        }
        // 获取key
        List<? extends R> keys = listMap2List(srcList, mapper);
        Map<R, T> retMap = new LinkedHashMap<>(srcList.size());

        Map<? extends R, T> tmpMap = list2Map(srcList, mapper);
        keys.forEach(k -> retMap.put(k, tmpMap.get(k)));

        return retMap;
    }

    /**
     * List转Map
     * 【注意】：value不能有null，否则转换失败
     *
     * @param srcList     原始List
     * @param keyMapper   List元素转Map的key
     * @param valueMapper List元素转Map的value
     * @param <S>         s
     * @param <K>         k
     * @param <V>         v
     * @return data
     */
    public static <S, K, V> Map<K, V> list2Map(List<S> srcList, Function<? super S, ? extends K> keyMapper, Function<? super S, ? extends V> valueMapper) {
        if (isEmpty(srcList)) {
            return getEmptyMap();
        }
        return srcList.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * List元素去重
     *
     * @param srcList   原始列表
     * @param eleMapper 元素转换器
     * @param <T>       t
     * @param <R>       r
     * @return data
     */
    public static <T, R> List<R> listDistinct(List<T> srcList, Function<? super T, ? extends R> eleMapper) {
        if (isEmpty(srcList)) {
            return getEmptyList();
        }
        return srcList.stream().map(eleMapper).distinct().collect(Collectors.toList());
    }

    /**
     * List元素去重
     *
     * @param srcList 原始列表
     * @param <T>     t
     * @return data
     */
    public static <T> List<T> listDistinct(List<T> srcList) {
        if (isEmpty(srcList)) {
            return getEmptyList();
        }
        return srcList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 合并Map的List值
     *
     * @param srcMap 初始map
     * @param <K>    k
     * @param <V>    v
     * @return data
     */
    public static <K, V> List<V> mapValue2List(Map<K, List<V>> srcMap) {
        if (isEmpty(srcMap)) {
            return getEmptyList();
        }
        List<V> retList = new LinkedList<>();
        srcMap.values().forEach(retList::addAll);
        return retList;
    }

    /**
     * 合并Map的值到List
     *
     * @param srcMap 初始map
     * @param <K>    k
     * @param <V>    v
     * @return data
     */
    public static <K, V> List<V> mapValueEle2List(Map<K, V> srcMap) {
        if (isEmpty(srcMap)) {
            return getEmptyList();
        }
        return Lists.newArrayList(srcMap.values());
    }

    /**
     * 合并Map的List值
     *
     * @param srcMap 初始map
     * @param <K>    k
     * @param <V>    v
     * @return data
     */
    public static <K, V, T> List<T> mapValue2List(Map<K, List<V>> srcMap, Class<T> tarEleClass) {
        if (isEmpty(srcMap)) {
            return getEmptyList();
        }

        List<T> retList = new LinkedList<>();
        srcMap.values().forEach(vs -> {
            retList.addAll(BeanConvertUtil.convert(vs, tarEleClass));
        });

        return retList;
    }

    /**
     * Map中List值的元素类型转换
     *
     * @param srcMap      原始map
     * @param tarEleClass 元素转换后类型
     * @param <K>         k
     * @param <V>         v
     * @param <T>         t
     * @return data
     */
    public static <K, V, T> Map<K, List<T>> mapValueTypeConvert(Map<K, List<V>> srcMap, Class<T> tarEleClass) {
        if (isEmpty(srcMap)) {
            return getEmptyMap();
        }

        HashMap<K, List<T>> retMap = new HashMap<>(srcMap.size());
        Iterator<Map.Entry<K, List<V>>> iterator = srcMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, List<V>> kv = iterator.next();
            // value元素类型转换
            retMap.put(kv.getKey(), BeanConvertUtil.convert(kv.getValue(), tarEleClass));
        }

        return retMap;
    }

    /**
     * Map中List值的元素类型转换
     *
     * @param srcMap         原始map
     * @param valueEleMapper 元素映射关系
     * @param <K>            k
     * @param <V>            v
     * @param <T>            t
     * @return data
     */
    public static <K, V, T> Map<K, List<T>> mapValueTypeConvert(Map<K, List<V>> srcMap, Function<? super V, ? extends T> valueEleMapper) {
        if (isEmpty(srcMap)) {
            return getEmptyMap();
        }
        Map<K, List<T>> retMap = new HashMap<>(srcMap.size());
        Iterator<Map.Entry<K, List<V>>> iterator = srcMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, List<V>> kv = iterator.next();
            retMap.put(kv.getKey(), listMap2List(kv.getValue(), valueEleMapper));
        }
        return retMap;
    }

    /**
     * Map中值元素转换
     *
     * @param srcMap         原始map
     * @param valueEleMapper 值转换器
     * @param <K>            k
     * @param <V>            v
     * @param <T>            t
     * @return data
     */
    public static <K, V, T> Map<K, T> mapValueConvert(Map<K, V> srcMap, Function<? super V, ? extends T> valueEleMapper) {
        if (isEmpty(srcMap)) {
            return getEmptyMap();
        }
        Map<K, T> retMap = new HashMap<>(srcMap.size());
        Iterator<Map.Entry<K, V>> iterator = srcMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> kv = iterator.next();
            retMap.put(kv.getKey(), valueEleMapper.apply(kv.getValue()));
        }
        return retMap;
    }

    /**
     * 过滤满足条件的元素
     *
     * @param srcList 原始List
     * @param filter  过滤条件
     * @param <T>     t
     * @return data
     */
    public static <T> List<T> filter(List<T> srcList, Predicate<T> filter) {
        if (isEmpty(srcList)) {
            return getEmptyList();
        }
        return srcList.stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * 过滤满足条件的元素
     *
     * @param srcMap 原始map
     * @param filter 根据key过滤条件
     * @param <K>    k
     * @param <V>    v
     * @return data
     */
    public static <K, V> Map<K, V> filter(Map<K, V> srcMap, Predicate<K> filter) {
        if (isEmpty(srcMap)) {
            return getEmptyMap();
        }
        Map<K, V> retMap = new HashMap<>(16);
        srcMap.entrySet().forEach(entry -> {
            if (filter.test(entry.getKey())) {
                retMap.put(entry.getKey(), entry.getValue());
            }
        });

        return retMap;
    }

    /**
     * 过滤并获取符合条件的元素个数
     *
     * @param srcList 元素列表
     * @param filter  过滤器
     * @param <T>     t
     * @return data
     */
    public static <T> int filterAndCount(List<T> srcList, Predicate<T> filter) {
        return filter(srcList, filter).size();
    }

    private static boolean isEmpty(Map map) {
        return MapUtils.isEmpty(map);
    }

    private static boolean isEmpty(Collection list) {
        return CollectionUtils.isEmpty(list);
    }

    private static List getEmptyList() {
        return Lists.newArrayList();
    }

    private static Set getEmptySet() {
        return Sets.newHashSet();
    }

    private static Map getEmptyMap() {
        return Maps.newHashMap();
    }

}
