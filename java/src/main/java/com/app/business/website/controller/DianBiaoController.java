package com.app.business.website.controller;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.app.business.website.entity.DianBiao;
import com.app.business.website.service.DianBiaoService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 电表设备 控制层。
 *
 * @author CodeGenerator
 * @since 2025-11-24
 */
@RestController
@RequestMapping("/dianBiao")
//@UseDataSource("ds2")
public class DianBiaoController {

    @Autowired
    private DianBiaoService dianBiaoService;

    /**
     * 保存电表设备。
     *
     * @param dianBiao 电表设备
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody DianBiao dianBiao) {
        return dianBiaoService.save(dianBiao);
    }

    /**
     * 根据主键删除电表设备。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return dianBiaoService.removeById(id);
    }

    /**
     * 根据主键更新电表设备。
     *
     * @param dianBiao 电表设备
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody DianBiao dianBiao) {
        return dianBiaoService.updateById(dianBiao);
    }

    /**
     * 查询所有电表设备。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<DianBiao> list() {
        return dianBiaoService.list();
    }

    /**
     * 根据主键获取电表设备。
     *
     * @param id 电表设备主键
     * @return 电表设备详情
     */
    @GetMapping("getInfo/{id}")
    public DianBiao getInfo(@PathVariable String id) {
        return dianBiaoService.getById(id);
    }

    /**
     * 分页查询电表设备。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<DianBiao> page(Page<DianBiao> page) {
        return dianBiaoService.page(page);
    }

}
