package com.codermast.takeoutfood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codermast.takeoutfood.common.BaseContext;
import com.codermast.takeoutfood.common.R;
import com.codermast.takeoutfood.entity.ShoppingCart;
import com.codermast.takeoutfood.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: 购物控制器
 * @author: CoderMast
 * @date: 2022/11/30
 * @Blog: <a href="https://www.codermast.com/">codermast</a>
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * @Description: 获取购物车列表
     * @Author: <a href="https://www.codermast.com/">CoderMast</a>
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * @Description: 将菜品添加到购物车
     * @param shoppingCart 添加的菜品封装对象
     * @Author: <a href="https://www.codermast.com/">CoderMast</a>
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        // 在购物车中查询是否存在该菜品
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);

        // 购物车存在该菜品，则仅增加该菜品的数量即可
        if (shoppingCartServiceOne != null){
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {
            shoppingCartService.save(shoppingCart);
        }
        return  R.success(shoppingCart);
    }

    /**
     * @Description: 减少一个菜品的数量，数量为1时减少即为删除该菜品
     * @param map 数据封装
     * @Author: <a href="https://www.codermast.com/">CoderMast</a>
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map<String,String> map){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(ShoppingCart::getSetmealId,map.get("setmealId"));

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one.getNumber() > 1){
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCartService.remove(queryWrapper);
        }
        return R.success("删除成功");
    }

    /**
     * @Description: 清空购物车
     * @Author: <a href="https://www.codermast.com/">CoderMast</a>
     */
    @DeleteMapping("/clean")
    public R<String> sub(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
