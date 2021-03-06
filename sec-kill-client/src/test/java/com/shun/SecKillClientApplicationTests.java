package com.shun;

import com.shun.dao.SnapProductDao;
import com.shun.entity.SnapProduct;
import com.shun.feign.ProductFeign;
import com.shun.service.SnapProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class SecKillClientApplicationTests {

    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private SnapProductService snapProductService;
    @Autowired
    private SnapProductDao snapProductDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        Map map = snapProductService.findAll(10, 1);
        System.out.println(map);
    }
    @Test
    public void testDel(){
        Boolean aBoolean = snapProductService.deleteById(3);
        System.out.println(aBoolean);
    }
    @Test
    public void testFeign(){
        Map map = productFeign.testFeigns(10, 1);
        System.out.println(map);
    }
    @Test
    public void testDao() {
        List<SnapProduct> snap = snapProductDao.findByPage(0, 10);
        for (SnapProduct snapProduct : snap) {
            System.out.println(snapProduct);
        }
    }
    @Test
    public void testTask() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String dateStr = sdf.format(date);
        Date HourDate = sdf.parse(dateStr);
        Map map = snapProductService.searchByTime(HourDate, 1, 1000);
        System.out.println(map);
        List<SnapProduct> snapProducts = (List<SnapProduct>) map.get("rows");
        System.out.println(snapProducts);
        if (snapProducts != null && snapProducts.size() > 0) {
            ListOperations<String, String> stringStringListOperations = stringRedisTemplate.opsForList();
            snapProducts.forEach(snapProduct -> {
                if (snapProduct.getVolume() < snapProduct.getCount()) {
                    int num = snapProduct.getCount()-snapProduct.getVolume();
                    List<String> nums = new ArrayList<>();
                    for (int i = 0;  i<num ; i++) {
                        nums.add("1");
                    }
                    stringStringListOperations.leftPushAll("snapCount_id="+snapProduct.getId(),nums);
                }
            });
        }
    }
    @Test
    public void testRedisDel(){
        Set<String> keys = stringRedisTemplate.keys("snapCount*");
        if(keys!=null){
            stringRedisTemplate.delete(keys);
        }
    }
}
