/**

 @Name：layuiAdmin 内容系统
 @Author：star1029
 
 @License：LPPL
    
 */


layui.define(['table', 'form'], function(exports){
  var $ = layui.$
  ,table = layui.table
  ,setter = layui.setter
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
        ,area: ['550px', '580px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            
            if(field.tmpPath == '' || field.tmpPath == null){
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
    ,headers: {
      'teacher_access_token': layui.data(setter.tableName).teacher_access_token
    }
    ,initSort: {
      field: 'id' //排序字段，对应 cols 设定的各字段名
      ,type: 'desc' //排序方式  asc: 升序、desc: 降序、null: 默认排序
    }
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: '作业ID', sort: true}
      ,{field: 'name', title: '作业名称', minWidth: 100}
      ,{field: 'subjectId', title: '作业科目', minWidth: 100, hide: true}
      ,{field: 'subjectName', title: '作业科目', minWidth: 100}
      ,{field: 'submitted', title: '上交人数'}
      ,{field: 'total', title: '总人数'}
      ,{field: 'deadline', title: '截止日期', sort: true}
      ,{title: '上交详情', minWidth: 150, align: 'center', fixed: 'right', toolbar: '#table-homework-list1'}
      ,{title: '操作', minWidth: 270, align: 'center', fixed: 'right', toolbar: '#table-homework-list2'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //作业列表操作管理监听工具条
  table.on('tool(LAY-app-homework-list)', function(obj){
    var data = obj.data;


    if(obj.event === 'show'){
      parent.layui.index.openTabsPage('details/list.html?'
      + "homeworkId="+ data.id
      + "&submitted="+ data.submitted
      + "&total="+ data.total
      + "&name="+ data.name
      , '作业详情')
      
    }else if(obj.event === 'del'){
        layer.confirm('确定删除此作业吗？', function(index){
          //请求服务器删除
          admin.req({
            url: layui.setter.reqUrl + '/homework/del' //实际使用请改成服务端真实接口
            ,data: "[" + JSON.stringify(data) + "]"
            ,type: 'post'
            ,contentType:'application/json'
            ,done: function(res){
              //从表格中删除
              obj.del();
              layer.msg('已删除');
            }
          });
          layer.close(index);

        });
    } else if(obj.event === 'down'){
      window.open(layui.setter.reqUrl + "/homework/down?" 
      + "filePath="+ data.filePath
      + "&fileName="+ data.fileName
      + "&fileRandomName="+ data.fileRandomName);
    } else if(obj.event === 'edit'){
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '编辑作业'
        ,content: '../../../views/teacher/homework/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '580px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){
            var field = data.field; //获取提交的字段
            console.log(field)
            admin.req({
              url: layui.setter.reqUrl + '/homework/update' //实际使用请改成服务端真实接口
              ,data: field
              ,type: 'post'
              ,done: function(res){
                layer.close(index);//再执行关闭
                layer.msg('作业修改成功');
              }
            });        
          });  
          
          submit.trigger('click');
        }
      });
    }
  });

  
  
  //老师作业详情列表操作管理监听工具条
  table.on('tool(LAY-app-details-list)', function(obj){
    var data = obj.data;
    
    if(obj.event === 'check'){
      admin.req({
        url: layui.setter.reqUrl + '/homework/checkOne' //实际使用请改成服务端真实接口
        ,data: data
        ,type: 'post'
        ,done: function(res){
        
          layui.table.reload('LAY-app-details-list'); //重载表格
          layer.msg('查重完毕');

        }
      });  
    } else if(obj.event === 'down'){
      window.open(layui.setter.reqUrl + "/homework/down?" 
      + "filePath="+ data.studentFilePath
      + "&fileName="+ data.studentFileName
      + "&fileRandomName="+ data.studentFileRandomName);
    } else if(obj.event === 'checkDetails'){

      console.log(data)

      window.open(layui.setter.reqUrl + "/homework/down?"
          + "filePath="+ data.filePath + data.homeworkName + "-查重结果/" + data.clazzName + "/"
          + "&fileName="+ data.studentFileName + ".html"
          + "&fileRandomName="+ data.studentFileRandomName + ".html");
      // parent.layui.index.openTabsPage(layui.setter.reqUrl + "/homework/down?"
      //     + "filePath="+ data.filePath + data.homeworkName + "-查重结果/" + data.clazzName + "/"
      //     + "&fileName="+ data.studentFileName + ".html"
      //     + "&fileRandomName="+ data.studentFileRandomName + ".html")
    }


  });

  exports('teacherlist', {})
});