package cn.iocoder.yudao.module.system.service.student;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.system.controller.admin.student.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.student.StudentDO;
import cn.iocoder.yudao.module.system.dal.dataobject.student.StudentCourseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.student.StudentGradeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.system.dal.mysql.student.StudentMapper;
import cn.iocoder.yudao.module.system.dal.mysql.student.StudentCourseMapper;
import cn.iocoder.yudao.module.system.dal.mysql.student.StudentGradeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 学生 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private StudentCourseMapper studentCourseMapper;
    @Resource
    private StudentGradeMapper studentGradeMapper;

    @Override
    public Long createStudent(StudentSaveReqVO createReqVO) {
        // 插入
        StudentDO student = BeanUtils.toBean(createReqVO, StudentDO.class);
        studentMapper.insert(student);
        // 返回
        return student.getId();
    }

    @Override
    public void updateStudent(StudentSaveReqVO updateReqVO) {
        // 校验存在
        validateStudentExists(updateReqVO.getId());
        // 更新
        StudentDO updateObj = BeanUtils.toBean(updateReqVO, StudentDO.class);
        studentMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        // 校验存在
        validateStudentExists(id);
        // 删除
        studentMapper.deleteById(id);

        // 删除子表
        deleteStudentCourseByStudentId(id);
        deleteStudentGradeByStudentId(id);
    }

    private void validateStudentExists(Long id) {
        if (studentMapper.selectById(id) == null) {
            throw exception(STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public StudentDO getStudent(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public PageResult<StudentDO> getStudentPage(StudentPageReqVO pageReqVO) {
        return studentMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（学生课程） ====================

    @Override
    public PageResult<StudentCourseDO> getStudentCoursePage(PageParam pageReqVO, Long studentId) {
        return studentCourseMapper.selectPage(pageReqVO, studentId);
    }

    @Override
    public Long createStudentCourse(StudentCourseDO studentCourse) {
        studentCourseMapper.insert(studentCourse);
        return studentCourse.getId();
    }

    @Override
    public void updateStudentCourse(StudentCourseDO studentCourse) {
        // 校验存在
        validateStudentCourseExists(studentCourse.getId());
        // 更新
        studentCourse.setUpdater(null).setUpdateTime(null); // 解决更新情况下：updateTime 不更新
        studentCourseMapper.updateById(studentCourse);
    }

    @Override
    public void deleteStudentCourse(Long id) {
        // 校验存在
        validateStudentCourseExists(id);
        // 删除
        studentCourseMapper.deleteById(id);
    }

    @Override
    public StudentCourseDO getStudentCourse(Long id) {
        return studentCourseMapper.selectById(id);
    }

    private void validateStudentCourseExists(Long id) {
        if (studentCourseMapper.selectById(id) == null) {
            throw exception(STUDENT_COURSE_NOT_EXISTS);
        }
    }

    private void deleteStudentCourseByStudentId(Long studentId) {
        studentCourseMapper.deleteByStudentId(studentId);
    }

    // ==================== 子表（学生班级） ====================

    @Override
    public PageResult<StudentGradeDO> getStudentGradePage(PageParam pageReqVO, Long studentId) {
        return studentGradeMapper.selectPage(pageReqVO, studentId);
    }

    @Override
    public Long createStudentGrade(StudentGradeDO studentGrade) {
        // 校验是否已经存在
        if (studentGradeMapper.selectByStudentId(studentGrade.getStudentId()) != null) {
            throw exception(STUDENT_GRADE_EXISTS);
        }
        // 插入
        studentGradeMapper.insert(studentGrade);
        return studentGrade.getId();
    }

    @Override
    public void updateStudentGrade(StudentGradeDO studentGrade) {
        // 校验存在
        validateStudentGradeExists(studentGrade.getId());
        // 更新
        studentGrade.setUpdater(null).setUpdateTime(null); // 解决更新情况下：updateTime 不更新
        studentGradeMapper.updateById(studentGrade);
    }

    @Override
    public void deleteStudentGrade(Long id) {
        // 校验存在
        validateStudentGradeExists(id);
        // 删除
        studentGradeMapper.deleteById(id);
    }

    @Override
    public StudentGradeDO getStudentGrade(Long id) {
        return studentGradeMapper.selectById(id);
    }

    private void validateStudentGradeExists(Long id) {
        if (studentGradeMapper.selectById(id) == null) {
            throw exception(STUDENT_GRADE_NOT_EXISTS);
        }
    }

    private void deleteStudentGradeByStudentId(Long studentId) {
        studentGradeMapper.deleteByStudentId(studentId);
    }

}