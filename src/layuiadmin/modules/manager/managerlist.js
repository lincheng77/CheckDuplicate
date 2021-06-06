/**

 @Name：layuiAdmin 内容系统
 @Author：star1029
 
 @License：LPPL
    
 */
//  ,success: function (layero, index) {


// }
          //为子窗口表单赋值开始---------------------------------------------------
          // //找到它的子窗口的body
          // var body = layer.getChildFrame('body', index);  //巧妙的地方在这里哦
          // //为子窗口元素赋值
          // var record = obj.data;
          // body.contents().find("#id").val(record.id);
          // body.contents().find("#name").val(record.name);
          // body.contents().find("#grade").val(record.grade);
          // body.contents().find("#college").val(record.college);
          // body.contents().find("#counselor").val(record.counselor);
          // body.contents().find("#state").val(record.state);
          // if(record.state == 1){
          //   body.contents().find("#state").prop("checked", true);
          //   body.form.render();
          // }
          //为子窗口表单赋值结束---------------------------------------------------

 layui.define(['table', 'form'], function(exports){
  var $ = layui.$
  ,table = layui.table
  ,admin = layui.admin
  ,form = layui.form;
  //班级管理
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
  
  //班级管理监听工具条
  table.on('tool(LAY-app-content-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'del'){
      console.log(data)
      console.log(JSON.stringify(data))
      layer.confirm('确定删除此文章？', function(index){
        //请求服务器删除
        admin.req({
          url: layui.setter.reqUrl + '/clazz/del' //实际使用请改成服务端真实接口
          ,data: "[" + JSON.stringify(data) + "]"
          ,type: 'post'
          ,contentType:'application/json'
          ,done: function(res){

            //从表格中删除
            obj.del();
            layer.msg('已删除');
          }
        });

        // //从表格中删除
        // obj.del();

        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '编辑班级'
        ,content: '../../../views/manager/clazz/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '550px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            //提交 Ajax 成功后，静态更新表格中的数据
            //$.ajax({});请求服务器删除
            if(field.state == null){
              field.state = 0;
            }
            admin.req({
              url: layui.setter.reqUrl + '/clazz/update' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                // obj.update({
                //   grade: field.grade
                //   ,name: field.name
                //   ,grade: field.college
                //   ,counselor: field.counselor
                //   ,state: field.state
                  
                // }); //数据更新
                layui.table.reload('LAY-app-content-list'); //重载表格

                layer.msg('已更新');
              }
            });        
            // obj.update({
            //   label: field.label
            //   ,title: field.title
            //   ,author: field.author
            //   ,status: field.status
            // }); //数据更新
            
            form.render();
            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
      });
    }
  });

  //学科管理
  table.render({
    elem: '#LAY-app-subject-list'
    ,url: layui.setter.reqUrl + '/subject/listByPage' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 200, title: '学科ID', sort: true}
      ,{field: 'name', title: '学科名称', width: 300}
      ,{field: 'introduction', title: '学科介绍', minWidth: 100}
      ,{field: 'state', title: '状态', templet: '#buttonTpl', width: 120, align: 'center'}
      ,{title: '操作', width: 250, align: 'center', fixed: 'right', toolbar: '#table-subject-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //学科管理监听工具条
  table.on('tool(LAY-app-subject-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'del'){
      console.log(data)
      console.log(JSON.stringify(data))
      layer.confirm('确定删除此文章？', function(index){
        //请求服务器删除
        admin.req({
          url: layui.setter.reqUrl + '/subject/del' //实际使用请改成服务端真实接口
          ,data: "[" + JSON.stringify(data) + "]"
          ,type: 'post'
          ,contentType:'application/json'
          ,done: function(res){

            //从表格中删除
            obj.del();
            layer.msg('已删除');
          }
        });

        // //从表格中删除
        // obj.del();

        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '编辑班级'
        ,content: '../../../views/manager/subject/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '550px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            //提交 Ajax 成功后，静态更新表格中的数据
            //$.ajax({});请求服务器删除
            if(field.state == null){
              field.state = 0;
            }
            admin.req({
              url: layui.setter.reqUrl + '/subject/update' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                layui.table.reload('LAY-app-subject-list'); //重载表格
                layer.msg('已更新');
              }
            });        
            form.render();
            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
      });
    }
  });

  //教师管理
  table.render({
    elem: '#LAY-app-teacher-list'
    ,url: layui.setter.reqUrl + '/teacher/listByPageForManager' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 200, title: '教师ID', sort: true}
      ,{field: 'username', title: '教师账号（工号）', minWidth: 300}
      ,{field: 'name', title: '教师名称', minWidth: 300}
      ,{field: 'state', title: '状态', templet: '#buttonTpl', minWidth: 120, align: 'center'}
      ,{title: '操作', width: 250, align: 'center', fixed: 'right', toolbar: '#table-teacher-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //教师管理监听工具条
  table.on('tool(LAY-app-teacher-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'del'){
      console.log(data)
      console.log(JSON.stringify(data))
      layer.confirm('确定删除此教师账号吗？', function(index){
        //请求服务器删除
        admin.req({
          url: layui.setter.reqUrl + '/teacher/delForManager' //实际使用请改成服务端真实接口
          ,data: "[" + JSON.stringify(data) + "]"
          ,type: 'post'
          ,contentType:'application/json'
          ,done: function(res){

            //从表格中删除
            obj.del();
            layer.msg('已删除');
          }
        });

        // //从表格中删除
        // obj.del();

        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '编辑教师'
        ,content: '../../../views/manager/teacher/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '550px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            //提交 Ajax 成功后，静态更新表格中的数据
            //$.ajax({});请求服务器删除
            if(field.state == null){
              field.state = 0;
            }
            admin.req({
              url: layui.setter.reqUrl + '/teacher/updateForManager' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                layui.table.reload('LAY-app-teacher-list'); //重载表格
                layer.msg('已更新');
              }
            });        
            form.render();
            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
      });
    }
  });

  //学生管理
  table.render({
    elem: '#LAY-app-student-list'
    ,url: layui.setter.reqUrl + '/student/listByPageForManager' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 200, title: '学生ID', sort: true}
      ,{field: 'username', title: '学生账号（学号）', minWidth: 300}
      ,{field: 'name', title: '学生姓名', minWidth: 300}
      ,{field: 'clazzName', title: '学生班级', minWidth: 300}
      ,{field: 'state', title: '状态', templet: '#buttonTpl', minWidth: 120, align: 'center'}
      ,{title: '操作', width: 250, align: 'center', fixed: 'right', toolbar: '#table-student-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //教师管理监听工具条
  table.on('tool(LAY-app-student-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'del'){
      console.log(data)
      console.log(JSON.stringify(data))
      layer.confirm('确定删除此教师账号吗？', function(index){
        //请求服务器删除
        admin.req({
          url: layui.setter.reqUrl + '/student/delForManager' //实际使用请改成服务端真实接口
          ,data: "[" + JSON.stringify(data) + "]"
          ,type: 'post'
          ,contentType:'application/json'
          ,done: function(res){

            //从表格中删除
            obj.del();
            layer.msg('已删除');
          }
        });

        // //从表格中删除
        // obj.del();

        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '编辑教师'
        ,content: '../../../views/manager/student/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '550px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            //提交 Ajax 成功后，静态更新表格中的数据
            //$.ajax({});请求服务器删除
            if(field.state == null){
              field.state = 0;
            }
            admin.req({
              url: layui.setter.reqUrl + '/student/updateForManager' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                layui.table.reload('LAY-app-student-list'); //重载表格
                layer.msg('已更新');
              }
            });        
            form.render();
            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
      });
    }
  });



  exports('managerlist', {})
});