package cn.iocoder.yudao.module.system.service.category;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.annotation.Resource;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;

import cn.iocoder.yudao.module.system.controller.admin.category.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.module.system.dal.mysql.category.CategoryMapper;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.*;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.*;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link CategoryServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(CategoryServiceImpl.class)
public class CategoryServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CategoryServiceImpl categoryService;

    @Resource
    private CategoryMapper categoryMapper;

    @Test
    public void testCreateCategory_success() {
        // 准备参数
        CategorySaveReqVO createReqVO = randomPojo(CategorySaveReqVO.class).setId(null);

        // 调用
        Long categoryId = categoryService.createCategory(createReqVO);
        // 断言
        assertNotNull(categoryId);
        // 校验记录的属性是否正确
        CategoryDO category = categoryMapper.selectById(categoryId);
        assertPojoEquals(createReqVO, category, "id");
    }

    @Test
    public void testUpdateCategory_success() {
        // mock 数据
        CategoryDO dbCategory = randomPojo(CategoryDO.class);
        categoryMapper.insert(dbCategory);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CategorySaveReqVO updateReqVO = randomPojo(CategorySaveReqVO.class, o -> {
            o.setId(dbCategory.getId()); // 设置更新的 ID
        });

        // 调用
        categoryService.updateCategory(updateReqVO);
        // 校验是否更新正确
        CategoryDO category = categoryMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, category);
    }

    @Test
    public void testUpdateCategory_notExists() {
        // 准备参数
        CategorySaveReqVO updateReqVO = randomPojo(CategorySaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> categoryService.updateCategory(updateReqVO), CATEGORY_NOT_EXISTS);
    }

    @Test
    public void testDeleteCategory_success() {
        // mock 数据
        CategoryDO dbCategory = randomPojo(CategoryDO.class);
        categoryMapper.insert(dbCategory);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbCategory.getId();

        // 调用
        categoryService.deleteCategory(id);
       // 校验数据不存在了
       assertNull(categoryMapper.selectById(id));
    }

    @Test
    public void testDeleteCategory_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> categoryService.deleteCategory(id), CATEGORY_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCategoryList() {
       // mock 数据
       CategoryDO dbCategory = randomPojo(CategoryDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setParentId(null);
           o.setCreateTime(null);
       });
       categoryMapper.insert(dbCategory);
       // 测试 name 不匹配
       categoryMapper.insert(cloneIgnoreId(dbCategory, o -> o.setName(null)));
       // 测试 parentId 不匹配
       categoryMapper.insert(cloneIgnoreId(dbCategory, o -> o.setParentId(null)));
       // 测试 createTime 不匹配
       categoryMapper.insert(cloneIgnoreId(dbCategory, o -> o.setCreateTime(null)));
       // 准备参数
       CategoryListReqVO reqVO = new CategoryListReqVO();
       reqVO.setName(null);
       reqVO.setParentId(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       List<CategoryDO> list = categoryService.getCategoryList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbCategory, list.get(0));
    }

}