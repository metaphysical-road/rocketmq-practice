package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.entity.*;
import com.rocketmq.cloud.youxia.manager.*;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableScheduling
public class GoodDataTask {

    @Autowired
    private CategoryManager categoryManager;

    @Autowired
    private BrandManager brandManager;

    @Autowired
    private GoodManager goodManager;

    @Autowired
    private PropertyNameManager propertyNameManager;

    @Autowired
    private PropertyValueManager propertyValueManager;

    @Autowired
    private GoodPropertyManager goodPropertyManager;

    @Autowired
    private GoodsSkuManager goodsSkuManager;

    @Autowired
    private GoodDetailManager goodDetailManager;

    @Autowired
    private GoodsImageManager goodsImageManager;

    @DubboReference(version = "1.0.0", group = "rocketmq-practice")
    private DistributedService distributedService;

    private volatile boolean isCategoryInit = false;

    private volatile boolean isBrandInit = false;

    private volatile boolean isGoodInit = false;

    //选择系列会去关联多个SKU，套装也会去关联一个SKU
    //比如选择系列可以对应多个值"Flink实战派"、"Spring Cloud实战派"、"Spring Cloud Alibaba(两册)、"分布式事务之Seata"等。
    //套装则可以对应"优惠套装1"，它是一个组合打包的SKU。
    private String[] propertyKey = new String[]{"重量", "选择系列", "增值业务", "套装", "增值保障", "白条分期"};

    private String[] propertyValue = new String[]{"1.735kg", "Spring Cloud Alibaba(两册);微服务项目实战派;RocketMQ分布式架构实战派", "助力环保",
            "优惠套装", "2年爱心收", "不分期;3期;6期"};

    private static Map<String, Map<String, String>> propertyKeyMap = new ConcurrentHashMap<>();

    static {
        Map<String, String> propertyValueMap = null;
        propertyValueMap = new HashMap<>();
        propertyValueMap.put("重量", "8.735kg");
        propertyValueMap.put("选择系列", "Spring Cloud Alibaba(两册);Flink实战派;Spring Cloud实战派;分布式事务之Seata");
        propertyValueMap.put("增值业务", "助力环保");
        propertyValueMap.put("套装", "优惠套装1");
        propertyValueMap.put("增值保障", "2年爱心收");
        propertyValueMap.put("白条分期", "不分期;3期;6期");
        propertyKeyMap.put("优惠套装1", propertyValueMap);
        propertyKeyMap.put("Spring Cloud Alibaba(两册)", propertyValueMap);
        propertyKeyMap.put("Flink实战派", propertyValueMap);
        propertyKeyMap.put("Spring Cloud实战派", propertyValueMap);
        propertyKeyMap.put("分布式事务之Seata", propertyValueMap);
    }

    private String[] skuList = new String[]{"优惠套装1", "Spring Cloud Alibaba(两册)", "Flink实战派", "Spring Cloud实战派", "分布式事务之Seata"};

    private Lock lock=new ReentrantLock();

    //暂时只创建一个商品
    @Scheduled(fixedRate = 1000)
    public void packageGoodData() {
        extracted();
    }

    @Transactional(rollbackForClassName = "Exception.class",propagation = Propagation.REQUIRED)
    public void extracted() {
        lock.lock();
        try {
            CategoryEntity goodCategory = null;
            BrandEntity goodBrand = null;
            //构造一个类目树
            if (isCategoryInit == false) {
                CategoryEntity categoryEntity1 = new CategoryEntity();
                categoryEntity1.setId(distributedService.nextId());
                categoryEntity1.setCategoryCode(distributedService.nextId());
                categoryEntity1.setLevel(0);
                categoryEntity1.setPid(0L);
                categoryEntity1.setName("图书");
                categoryEntity1.setGmtCreate(new Date());
                categoryEntity1.setGmtModified(new Date());
                categoryEntity1.setSort(0);
                categoryEntity1.setIsDeleted(0);
                CategoryEntity categoryEntity1FromDB = categoryManager.select("图书");
                if (categoryEntity1FromDB != null) {
                    categoryEntity1 = categoryEntity1FromDB;
                } else {
                    categoryManager.insert(categoryEntity1);
                }
                CategoryEntity categoryEntity2 = new CategoryEntity();
                categoryEntity2.setId(distributedService.nextId());
                categoryEntity2.setCategoryCode(distributedService.nextId());
                categoryEntity2.setLevel(1);
                categoryEntity2.setPid(categoryEntity1.getCategoryCode());
                categoryEntity2.setName("计算机和互联网");
                categoryEntity2.setGmtCreate(new Date());
                categoryEntity2.setGmtModified(new Date());
                categoryEntity2.setSort(0);
                categoryEntity2.setIsDeleted(0);
                CategoryEntity categoryEntity2FromDB = categoryManager.select("计算机和互联网");
                if (categoryEntity2FromDB != null) {
                    categoryEntity2 = categoryEntity2FromDB;
                } else {
                    categoryManager.insert(categoryEntity2);
                }
                CategoryEntity categoryEntity3 = new CategoryEntity();
                categoryEntity3.setId(distributedService.nextId());
                categoryEntity3.setCategoryCode(distributedService.nextId());
                categoryEntity3.setLevel(2);
                categoryEntity3.setPid(categoryEntity2.getCategoryCode());
                categoryEntity3.setName("程序语言设计");
                categoryEntity3.setGmtCreate(new Date());
                categoryEntity3.setGmtModified(new Date());
                categoryEntity3.setSort(0);
                categoryEntity3.setIsDeleted(0);
                CategoryEntity categoryEntity3FromDB = categoryManager.select("程序语言设计");
                if (categoryEntity2FromDB != null) {
                    categoryEntity3 = categoryEntity3FromDB;
                } else {
                    categoryManager.insert(categoryEntity3);
                    categoryEntity3 = categoryManager.select("程序语言设计");
                }
                goodCategory = categoryEntity3;
                isCategoryInit = true;
            }
            if(null==goodCategory){
                goodCategory=categoryManager.select("程序语言设计");
            }
            //构造一个品牌
            if (isBrandInit == false) {
                BrandEntity brandEntity = new BrandEntity();
                brandEntity.setId(distributedService.nextId());
                brandEntity.setBrandCode(distributedService.nextId());
                brandEntity.setName("电子工业出版社");
                brandEntity.setDescription("博文视点IT旗舰品牌");
                brandEntity.setGmtCreate(new Date());
                brandEntity.setGmtModified(new Date());
                brandEntity.setSort(0);
                brandEntity.setIsDeleted(0);
                BrandEntity brandEntityFromDB = brandManager.select("电子工业出版社");
                if (brandEntityFromDB != null) {
                    brandEntity = brandEntityFromDB;
                } else {
                    brandManager.insert(brandEntity);
                    brandEntity = brandManager.select("电子工业出版社");
                }
                goodBrand = brandEntity;
                isBrandInit = true;
            }
            if(null==goodBrand){
                goodBrand= brandManager.select("电子工业出版社");
            }

            //构造一个商品"Spring Cloud Alibaba微服务架构实战派"，会关联多个SKU商品，比如"Flink实战派"、"Spring Cloud实战派"、"Spring Cloud Alibaba(两册)、"分布式事务之Seata"
            //套装1，包括三个SKU商品
            if (isGoodInit == false) {
                //插入基础商品信息res_supplier_goods
                GoodEntity goodEntity = new GoodEntity();
                goodEntity.setId(distributedService.nextId());
                goodEntity.setGoodsId(distributedService.nextId());
                goodEntity.setBrandId(goodBrand.getBrandCode());
                goodEntity.setCateId(goodCategory.getCategoryCode());
                goodEntity.setGoodsCode("TX_" + RandomUtils.nextLong());
                goodEntity.setCostPrice(800L);
                goodEntity.setShopPrice(900L);
                goodEntity.setHasSku(1);
                goodEntity.setNumWarnThreshold(300);
                goodEntity.setNum(200);
                goodEntity.setGoodsName("Srping Cloud Alibaba微服务架构实战派");
                goodEntity.setMaxSalesPrice(1000L);
                goodEntity.setMinSalesPrice(850L);
                goodEntity.setStatus(0);
                goodEntity.setSupplierId(distributedService.nextId());
                goodEntity.setStockStatus(0);
                goodEntity.setTags("测试");
                goodEntity.setGmtCreate(new Date());
                goodEntity.setGmtModified(new Date());
                goodEntity.setIsDeleted(0);
                GoodEntity goodEntityFrom = goodManager.selectGoodByName("Srping Cloud Alibaba微服务架构实战派");
                if (null != goodEntityFrom) {
                    goodEntity = goodEntityFrom;
                    return;
                } else {
                    goodManager.insert(goodEntity);
                    goodEntity = goodManager.selectGoodByName("Srping Cloud Alibaba微服务架构实战派");
                }
                //插入res_property_name和res_property_value
                Map<PropertyNameEntity, List<PropertyValueEntity>> mappingRelation = new HashMap<>();
                Iterator<String> iterator = propertyKeyMap.keySet().iterator();
                while (iterator.hasNext()){
                    PropertyNameEntity propertyNameEntityFromBase =null;
                    List<PropertyValueEntity> propertyValueEntityList = new CopyOnWriteArrayList<>();
                    String itemName=iterator.next();
                    Map<String, String> itemValue=propertyKeyMap.get(itemName);
                    Iterator<String> iteratorChild=itemValue.keySet().iterator();
                    while (iteratorChild.hasNext()){
                        String itemChildName=iteratorChild.next();
                        String itemChildValue=itemValue.get(itemChildName);
                        Integer count = propertyNameManager.selectNumByName(itemChildName);
                        if (count <= 0) {
                            PropertyNameEntity propertyNameEntity = new PropertyNameEntity();
                            propertyNameEntity.setName(itemChildName);
                            propertyNameEntity.setId(distributedService.nextId());
                            propertyNameEntity.setCategoryId(goodCategory.getCategoryCode());
                            propertyNameEntity.setSort(0);
                            propertyNameEntity.setIsMust(0);
                            propertyNameEntity.setIsColor(0);
                            propertyNameEntity.setIsAllowAlias(0);
                            propertyNameEntity.setIsMulti(0);
                            propertyNameEntity.setIsSearch(0);
                            propertyNameEntity.setStatus(0);
                            propertyNameEntity.setSupplierId(goodEntity.getSupplierId());
                            propertyNameEntity.setIsInput(0);
                            propertyNameEntity.setIsKey(0);
                            propertyNameEntity.setIsEnum(0);
                            propertyNameEntity.setIsSale(0);
                            propertyNameEntity.setIsDeleted(0);
                            propertyNameEntity.setGmtCreate(new Date());
                            propertyNameEntity.setGmtModified(new Date());
                            propertyNameManager.insert(propertyNameEntity);
                        }
                        propertyNameEntityFromBase = propertyNameManager.selectByName(itemChildName);
                        if (itemChildValue.contains(";")) {
                            String[] childItem = itemChildValue.split(";");
                            for (String scop : childItem) {
                                Integer skuValueCount1 = propertyValueManager.selectNumByName(scop);
                                if (skuValueCount1 <= 0) {
                                    PropertyValueEntity propertyValueEntity = new PropertyValueEntity();
                                    propertyValueEntity.setId(distributedService.nextId());
                                    propertyValueEntity.setValue(itemChildValue);
                                    propertyValueEntity.setPropNameId(propertyNameEntityFromBase.getId());
                                    propertyValueEntity.setSort(0);
                                    propertyValueEntity.setGmtCreate(new Date());
                                    propertyValueEntity.setGmtModified(new Date());
                                    propertyValueEntity.setStatus(0);
                                    propertyValueEntity.setIsDeleted(0);
                                    propertyValueManager.insert(propertyValueEntity);
                                }
                                PropertyValueEntity fromDb = propertyValueManager.selectByName(scop);
                                propertyValueEntityList.add(fromDb);
                            }
                        } else {
                            Integer skuValueCount2 = propertyValueManager.selectNumByName(itemChildValue);
                            if (skuValueCount2 <= 0) {
                                PropertyValueEntity propertyValueEntity = new PropertyValueEntity();
                                propertyValueEntity.setId(distributedService.nextId());
                                propertyValueEntity.setValue(itemChildValue);
                                propertyValueEntity.setPropNameId(propertyNameEntityFromBase.getId());
                                propertyValueEntity.setSort(0);
                                propertyValueEntity.setGmtCreate(new Date());
                                propertyValueEntity.setGmtModified(new Date());
                                propertyValueEntity.setStatus(0);
                                propertyValueEntity.setIsDeleted(0);
                                propertyValueManager.insert(propertyValueEntity);
                            }
                            PropertyValueEntity fromDb = propertyValueManager.selectByName(itemChildValue);
                            propertyValueEntityList.add(fromDb);
                        }
                        mappingRelation.put(propertyNameEntityFromBase,propertyValueEntityList);
                    }
                }
                //创建seven_goods_property关系
                Iterator<PropertyNameEntity> propertyNameEntityIterator = mappingRelation.keySet().iterator();
                List<Long> goodPropertyIdList = new ArrayList<>();
                StringBuilder goodPropertyString = new StringBuilder();
                while (propertyNameEntityIterator.hasNext()) {
                    PropertyNameEntity propertyNameEntity = propertyNameEntityIterator.next();
                    Long propertyNameId = propertyNameEntity.getId();
                    List<PropertyValueEntity> propertyValueEntityList = mappingRelation.get(propertyNameEntity);
                    List<Long> propertyValueList = new CopyOnWriteArrayList<>();
                    if (CollectionUtils.isNotEmpty(propertyValueEntityList)) {
                        for (PropertyValueEntity propertyValueEntity : propertyValueEntityList) {
                            if(null!=propertyValueEntity){
                                propertyValueList.add(propertyValueEntity.getId());
                            }
                        }
                    }
                    for (Long item : propertyValueList) {
                        GoodsPropertyEntity goodsPropertyEntity = new GoodsPropertyEntity();
                        goodsPropertyEntity.setGoodsId(goodEntity.getGoodsId());
                        goodsPropertyEntity.setPropNameId(propertyNameId);
                        goodsPropertyEntity.setPropValueId(item);
                        goodsPropertyEntity.setId(distributedService.nextId());
                        goodsPropertyEntity.setSort(0);
                        goodsPropertyEntity.setGmtCreate(new Date());
                        goodsPropertyEntity.setGmtModified(new Date());
                        goodsPropertyEntity.setIsDeleted(0);
                        goodPropertyManager.insert(goodsPropertyEntity);
                        goodPropertyIdList.add(goodsPropertyEntity.getId());
                        if (StringUtils.isEmpty(goodPropertyString.toString())) {
                            goodPropertyString.append(goodsPropertyEntity.getId());
                        } else {
                            goodPropertyString.append("," + goodsPropertyEntity.getId());
                        }
                    }
                }
                List<Long> skuIdList=new CopyOnWriteArrayList<>();
                //创建五个SKU
                for (String s : skuList) {
                    GoodsSkuEntity goodsSkuEntity = new GoodsSkuEntity();
                    goodsSkuEntity.setGoodsId(goodEntity.getGoodsId());
                    goodsSkuEntity.setGoodsCode(distributedService.nextId());
                    goodsSkuEntity.setProperties(goodPropertyString.toString());
                    goodsSkuEntity.setCostPrice(new BigDecimal(700L));
                    goodsSkuEntity.setMaxSalesPrice(new BigDecimal(1000L));
                    goodsSkuEntity.setMinSalesPrice(new BigDecimal(750L));
                    goodsSkuEntity.setNum(1000);
                    goodsSkuEntity.setNumWarnThreshold(1000);
                    goodsSkuEntity.setShopPrice(new BigDecimal(800L));
                    goodsSkuEntity.setTitle(s);
                    goodsSkuEntity.setStatus(0);
                    goodsSkuEntity.setSort(0);
                    goodsSkuEntity.setGmtCreate(new Date());
                    goodsSkuEntity.setGmtModified(new Date());
                    goodsSkuEntity.setIsDeleted(0);
                    Integer count=goodsSkuManager.selectEntityNumByGoodsSkuEntity(goodsSkuEntity);
                    if(count<=0){
                        goodsSkuManager.insert(goodsSkuEntity);
                        skuIdList.add(goodsSkuEntity.getId());
                    }
                }
                for(Long s:goodPropertyIdList) {
                    //插入seven_goods_image
                    GoodsImageEntity goodsImageEntity = new GoodsImageEntity();
                    goodsImageEntity.setGoodsId(goodEntity.getGoodsId());
                    goodsImageEntity.setId(distributedService.nextId());
                    goodsImageEntity.setGoodsPropertyId(s);
                    goodsImageEntity.setGmtCreate(new Date());
                    goodsImageEntity.setGmtModified(new Date());
                    goodsImageEntity.setName("测试" + RandomUtils.nextLong());
                    goodsImageEntity.setIsDeleted(0);
                    goodsImageEntity.setSort(0);
                    goodsImageEntity.setDuration(0);
                    goodsImageEntity.setIsMaster(0);
                    goodsImageEntity.setHeight(new BigDecimal(90L));
                    goodsImageEntity.setWidth(new BigDecimal(200L));
                    goodsImageEntity.setUrl("测试");
                    goodsImageEntity.setMimeType(0);
                    goodsImageEntity.setPosition(0);
                    goodsImageEntity.setSkuId(skuIdList.get(RandomUtils.nextInt(5)));
                    goodsImageManager.insert(goodsImageEntity);
                }
                //插入seven_goods_detail
                GoodDetailEntity goodDetailEntity=new GoodDetailEntity();
                goodDetailEntity.setGoodsId(goodEntity.getGoodsId());
                goodDetailEntity.setId(distributedService.nextId());
                goodDetailEntity.setIsDeleted(0);
                goodDetailEntity.setGmtCreate(new Date());
                goodDetailEntity.setGmtModified(new Date());
                goodDetailEntity.setContent("测试");
                goodDetailEntity.setSummary("测试");
                goodDetailManager.insert(goodDetailEntity);
            }
        }finally {
            lock.unlock();
        }
    }
}
