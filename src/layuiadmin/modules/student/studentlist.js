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
      ,{field: 'submitted', title: '作业状态' , width: 120, templet: '#buttonTpl-submitted', align: 'center'}
      ,{field: 'isCheck', title: '查重情况', templet: '#buttonTpl-isCheck', minWidth: 80, align: 'center'}
      ,{field: 'deadline', title: '截止日期', width: 180 , align: 'center'}
      ,{title: '操作', minWidth: 230, align: 'center', fixed: 'right', templet: '#table-content-list', toolbar: '#table-content-list'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //班级管理监听工具条
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




  //分类管理
  table.render({
    elem: '#LAY-app-content-tags'
    ,url: layui.setter.base + 'json/content/tags.js' //模拟接口
    ,cols: [[
      {type: 'numbers', fixed: 'left'}
      ,{field: 'id', width: 100, title: 'ID', sort: true}
      ,{field: 'tags', title: '分类名', minWidth: 100}
      ,{title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#layuiadmin-app-cont-tagsbar'}
    ]]
    ,text: '对不起，加载出现异常！'
  });
  
  //监听工具条
  table.on('tool(LAY-app-content-tags)', function(obj){
    var data = obj.data;
    if(obj.event === 'del'){
      layer.confirm('确定删除此分类？', function(index){
        obj.del();
        layer.close(index);
      });
    } else if(obj.event === 'edit'){
      var tr = $(obj.tr);
      layer.open({
        type: 2
        ,title: '编辑分类'
        ,content: '../../../views/app/content/tagsform.html?id='+ data.id
        ,area: ['450px', '200px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          //获取iframe元素的值
          var othis = layero.find('iframe').contents().find("#layuiadmin-app-form-tags")
          ,tags = othis.find('input[name="tags"]').val();
          
          if(!tags.replace(/\s/g, '')) return;
          
          obj.update({
            tags: tags
          });
          layer.close(index);
        }
        ,success: function(layero, index){
          //给iframe元素赋值
          var othis = layero.find('iframe').contents().find("#layuiadmin-app-form-tags").click();
          othis.find('input[name="tags"]').val(data.tags);
        }
      });
    }
  });

  //评论管理
  table.render({
    elem: '#LAY-app-content-comm'
    ,url: layui.setter.base + 'json/content/comment.js' //模拟接口
    ,cols: [[
      {type: 'checkbox', fixed: 'left'}
      ,{field: 'id', width: 100, title: 'ID', sort: true}
      ,{field: 'reviewers', title: '评论者', minWidth: 100}
      ,{field: 'content', title: '评论内容', minWidth: 100}
      ,{field: 'commtime', title: '评论时间', minWidth: 100, sort: true}
      ,{title: '操作', width: 150, align: 'center', fixed: 'right', toolbar: '#table-content-com'}
    ]]
    ,page: true
    ,limit: 10
    ,limits: [10, 15, 20, 25, 30]
    ,text: '对不起，加载出现异常！'
  });
  
  //监听工具条
  table.on('tool(LAY-app-content-comm)', function(obj){
    var data = obj.data;
    if(obj.event === 'del'){
      layer.confirm('确定删除此条评论？', function(index){
        obj.del();
        layer.close(index);
      });
    } else if(obj.event === 'edit') {
      layer.open({
        type: 2
        ,title: '编辑评论'
        ,content: '../../../views/app/content/contform.html'
        ,area: ['450px', '300px']
        ,btn: ['确定', '取消']
        ,yes: function(index, layero){
          var iframeWindow = window['layui-layer-iframe'+ index]
          ,submitID = 'layuiadmin-app-comm-submit'
          ,submit = layero.find('iframe').contents().find('#'+ submitID);

          //监听提交
          iframeWindow.layui.form.on('submit('+ submitID +')', function(data){
            var field = data.field; //获取提交的字段
            
            //提交 Ajax 成功后，静态更新表格中的数据
            //$.ajax({});
            table.reload('LAY-app-content-comm'); //数据刷新
            layer.close(index); //关闭弹层
          });  
          
          submit.trigger('click');
        }
        ,success: function(layero, index){
          
        }
      });
    }
  });

  exports('studentlist', {})
});