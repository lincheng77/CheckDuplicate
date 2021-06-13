/**

 @Name：layuiAdmin 内容系统
 @Author：star1029
 
 @License：LPPL
    
 */

 layui.define(['table', 'form'], function(exports){
  var $ = layui.$
  ,table = layui.table
  ,admin = layui.admin
  ,upload = layui.upload
  ,setter = layui.setter
  ,form = layui.form;
  
  //学生端作业管理
  table.render({
    elem: '#LAY-app-homework-list'
    ,url: layui.setter.reqUrl + '/homework/listPageForStudent' //模拟接口
    ,method: 'post'
    ,headers: {
      'student_access_token': layui.data(setter.tableName).student_access_token
    }
    ,initSort: {
      field: 'id' //排序字段，对应 cols 设定的各字段名
      ,type: 'desc' //排序方式  asc: 升序、desc: 降序、null: 默认排序
    }
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: '作业-学生ID', hide: true}//隐藏
      ,{field: 'homeworkId', width: 120, title: '作业ID', sort : true} 
      ,{field: 'clazzId', hide: true}
      ,{field: 'clazzName', title: '班级名称', hide: true}
      ,{field: 'subjectName', title: '作业学科', minWidth: 170}
      ,{field: 'homeworkName', title: '作业名称', minWidth: 270}
      ,{field: 'content', title: '作业内容', hide: true} //隐藏
      ,{field: 'submitted', title: '作业状态', hide: true , width: 120, templet: '#buttonTpl-submitted', align: 'center'}
      ,{field: 'isCheck', title: '查重情况', templet: '#buttonTpl-isCheck', minWidth: 80, align: 'center'}
      ,{field: 'deadline', title: '截止日期', width: 180 , align: 'center'}
      ,{title: '操作', minWidth: 250, align: 'center', fixed: 'right', templet: '#table-content-list', toolbar: '#table-content-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  

  var submit_homework_data;

  //执行实例【文件上传】
  uploadInst = upload.render({
    elem: '#homework-file' //绑定元素
    ,url: layui.setter.reqUrl + '/homework/upload' //上传接口
    ,accept: 'file' //允许上传的文件类型
    ,done: function(res){
      if(res.code == 0){
        //上传完毕回调
        admin.req({
          url: layui.setter.reqUrl + '/homework/submitHomework' //实际使用请改成服务端真实接口
          ,type: 'post'
          ,data: {
            id: submit_homework_data.id
            ,homeworkId: submit_homework_data.homeworkId
            ,homeworkName: submit_homework_data.homeworkName
            ,clazzId: submit_homework_data.clazzId
            ,clazzName: submit_homework_data.clazzName
            ,filePath: submit_homework_data.filePath //老师作业目录
            ,studentFileName: res.data.fileName
            ,studentFileRandomName: res.data.fileRandomName
            ,tmpPath: res.data.tmpPath
          }
          ,done: function(res){
            layer.msg('上传完成！');
          }
        });

      }else{
        //请求异常回调
        layer.msg('上传文件失败，请务必重新上传文件！');
      }
    }
    ,error: function(){
      //请求异常回调
      layer.msg('上传文件失败，请务必重新上传文件！');
    }
  });


  //学生端作业监听工具条
  table.on('tool(LAY-app-homework-list)', function(obj){
    var data = obj.data;
    if(obj.event === 'submit-homework'){
      console.log("sssssss")

      submit_homework_data = data;
      $('#homework-file').click();
      console.log(submit_homework_data)
    }else if(obj.event === 'details'){//作业详情
      json = JSON.stringify(data)
      layer.open({
        type: 2
        ,title: '作业内容'
        ,content: '../../../views/student/homework/listform.html?id='+ data.id
        ,maxmin: true
        ,area: ['550px', '520px']
        ,btn: ['确定']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submit = layero.find('iframe').contents().find("#layuiadmin-app-form-edit");
          //监听提交
          iframeWindow.layui.form.on('submit(layuiadmin-app-form-edit)', function(data){

            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
      });
    } else if(obj.event === 'down'){
      console.log(data)

      window.open(layui.setter.reqUrl + "/homework/down?" 
            + "filePath="+ data.filePath
            + "&fileName="+ data.fileName
            + "&fileRandomName="+ data.fileRandomName);
    }

  });


  exports('studentlist', {})
});