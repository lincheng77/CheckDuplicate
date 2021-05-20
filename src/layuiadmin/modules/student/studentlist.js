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
  //学生端作业管理
  table.render({
    elem: '#LAY-app-homework-list'
    ,url: layui.setter.reqUrl + '/homework/listPageForStudent' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: '作业-学生ID', hide: true}//隐藏
      ,{field: 'homeworkId', width: 120, title: '作业ID'} 
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
  
  //学生端作业监听工具条
  table.on('tool(LAY-app-homework-list)', function(obj){
    var data = obj.data;
    
    //交作业
    if(obj.event === 'submit-homework'){
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
    
    //作业详情
    } else if(obj.event === 'details'){
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
      
      // var formDown = $('<form method="POST" action="' + layui.setter.reqUrl + 'homework/down' +'">');
      // console.log(formDown)
      // formDown.append($('<input type="hidden" name="filePath" value="'+ data.filePath +'">'));
      // $('body').append(formDown);
      // formDown.submit();
      window.open(layui.setter.reqUrl + "/homework/down?" 
            + "filePath="+ data.filePath
            + "&fileName="+ data.fileName
            + "&fileRandomName="+ data.fileRandomName);
    }
  });


  exports('studentlist', {})
});