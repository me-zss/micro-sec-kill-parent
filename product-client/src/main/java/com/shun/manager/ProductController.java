package com.shun.manager;

import com.shun.entity.Product;
import com.shun.entity.ProductCategory;
import com.shun.service.ProductCategoryService;
import com.shun.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("product-manager")
@Log4j2
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @RequestMapping("findByPage")
    @ResponseBody
    public Map findAll(Integer rows, Integer page, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin","*");
        log.info(page);
        return productService.findByPage(rows,page);
    }

    @RequestMapping("addOrUpdate")
    @ResponseBody
    public String addOrUpdate(Product product,Integer categoryId,HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin","*");
        product.setProductCategory(new ProductCategory(null,null,categoryId));
        if(product.getCover()!=null&&product.getCover().length()==0){
            product.setCover(null);
        }
        if(product.getId()!=null){
            System.out.println(product);
            productService.update(product);
            productCategoryService.updateByProductId(product.getProductCategory());
        }else{
            product.setVolume(0);
            product.setCreateDate(new Date());
            int id = productService.insert(product);
            log.info("id:"+id);
            ProductCategory productCategory = product.getProductCategory();
            productCategory.setProductId(id);
            log.info(productCategory);
            productCategoryService.insert(productCategory);
        }
        return "ok";
    }
    @RequestMapping("edit")
    @ResponseBody
    public Map edit(String oper,Integer[] id,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        Map map = new HashMap();
        if ("del".equals(oper)) {
            productService.del(id);
            productCategoryService.del(id);
        }
        map.put("status",200);
        return map;
    }
}
