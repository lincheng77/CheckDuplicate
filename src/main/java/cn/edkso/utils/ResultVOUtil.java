
package cn.edkso.utils;

import cn.edkso.checkduplicate.entry.Teacher;
import cn.edkso.checkduplicate.enums.ResultEnum;
import cn.edkso.checkduplicate.vo.ResultVO;
import cn.edkso.checkduplicate.vo.TokenVO;
import org.springframework.data.domain.Page;

import java.util.Date;


public class ResultVOUtil {

    /**
     * 返回失败消息【自定义消息】
     */
    public static ResultVO error(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(-1);
        resultVO.setMsg(msg);
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }

    /**
     * 返回失败消息【自定义状态，自定义消息】
     */
    public static ResultVO error(Integer status, String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(-1);
        resultVO.setMsg(msg);
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }

    /**
     * 返回失败消息【根据ResultEnum 自定义状态，自定义消息】
     */
    public static ResultVO error(ResultEnum resultEnum){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(-1);
        resultVO.setMsg(resultEnum.getMessage());
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }

    /**
     * 返回成功消息【自定义消息】
     */
    public static ResultVO success(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg(msg);
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }


    /**
     * 返回成功消息【success】
     * @param teacher
     * @param data
     */
    public static ResultVO success(){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("success");
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }



    /**
     * 返回成功的消息【自定义消息，[data]】
     */
    public static ResultVO success(String msg,Object data){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg(msg);
        resultVO.setData(data);
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }



    /**
     * 返回成功的消息【success, [data]】
     */
    public static ResultVO success(Object data){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("success");
        resultVO.setData(data);
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }

    /**
     * 返回成功的消息【success, [page ->(count, data)]】
     */
    public static ResultVO success(Page page){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("success");
        resultVO.setCount(page.getTotalElements());
        resultVO.setData(page.getContent());

        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }

    @Deprecated
    public static ResultVO success(Object data, TokenVO tokenVO) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("success");
        resultVO.setData(data);
        resultVO.setAccess_token(tokenVO.getAccess_token());
        resultVO.setTimestamp(new Date().getTime());
        return resultVO;
    }
}
