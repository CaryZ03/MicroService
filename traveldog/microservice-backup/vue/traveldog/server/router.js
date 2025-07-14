const express = require('express')
const router = express.Router()
//导入数据库sqlFun('sql',[],res=>{})
const sqlFn = require('./mysql')
//路由接口
/**
 * 票列表：获取分页 {total:'',arr:[{},{},{}],pagesize:8,}
 * 参数：page 页码
 */
// 假设每页显示10条记录  
const PAGE_SIZE = 10;
router.get('/projectList', (req, res) => {
    const page = req.query.page || 1;
    const offset = (page - 1) * PAGE_SIZE;
    // 第一步：查询总记录数  
    const sqlCount = 'SELECT COUNT(*) AS total FROM trains';
    sqlFn(sqlCount, [], (totalCountResult) => {
        const total = totalCountResult[0].total; // 假设返回的是一个数组，第一个元素是对象，包含total字段  
        const totalPages = Math.ceil(total / PAGE_SIZE);
        // 第二步：查询分页数据  
        const sql = 'SELECT * FROM trains ORDER BY TrainNumber ASC LIMIT ? OFFSET ?';
        sqlFn(sql, [PAGE_SIZE, offset], (result) => {
            if (result.length > 0) {
                res.send({
                    status: 200,
                    data: result,
                    pageSize: PAGE_SIZE,
                    total: total,
                    current_page: page,
                    total_pages: totalPages
                });
            } else {
                res.send({
                    status: 500,
                    msg: "暂无数据"
                });
            }
        });
    });
});

module.exports = router