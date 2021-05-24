/**

 @Name：layuiAdmin 内容系统
 @Author：star1029
 
 @License：LPPL
    
 */


layui.define(['table', 'form'], function(exports){
  var $ = layui.$
  ,table = layui.table
  ,admin = layui.admin
  ,form = layui.form;
  //班级列表
  table.render({
    elem: '#LAY-app-content-list'
    ,url: layui.setter.reqUrl + '/clazz/listByPage' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: '班级ID', sort: true}
      ,{field: 'name', title: '班级名称', minWidth: 100}
      ,{field: 'grade', title: '班级年级'}
      ,{field: 'college', title: '学院'}
      ,{field: 'counselor', title: '辅导员', sort: true}
      ,{field: 'state', title: '状态', templet: '#buttonTpl', minWidth: 80, align: 'center'}
      ,{title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-content-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //班级列表操作管理监听工具条
  table.on('tool(LAY-app-content-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'del'){

    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '发布作业'
        ,content: '../../../views/teacher/clazz/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '550px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            if(field.filePath == '' || field.filePath == null){
              return layer.msg('请先上传文件');
            }
            admin.req({
              url: layui.setter.reqUrl + '/homework/add' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                layer.close(index);//再执行关闭
                layer.msg('作业发布成功');
              }
            });        
          });  
          
          submit.trigger('click');
        }
      });
    }
  });


  //作业管理
  table.render({
    elem: '#LAY-app-homework-list'
    ,url: layui.setter.reqUrl + '/homework/listByPage' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: '作业ID', sort: true}
      ,{field: 'name', title: '作业名称', minWidth: 100}
      ,{field: 'name', title: '作业科目', minWidth: 100}
      ,{field: 'grade', title: '上交人数'}
      ,{field: 'college', title: '总人数'}
      ,{field: 'counselor', title: '截止日期', sort: true}
      ,{title: '上交详情', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-homework-list1'}
      ,{title: '操作', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-homework-list2'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  exports('teacherlist', {})
});