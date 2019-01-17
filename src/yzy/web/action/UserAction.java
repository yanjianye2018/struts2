package yzy.web.action;

import cn.itcast.utils.CommonUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import yzy.domain.User;
import yzy.service.UserService;

import javax.servlet.ServletContext;
import java.io.*;

/**
 * @author yzy
 */
public class UserAction extends ActionSupport implements ModelDriven<User> {
    private User user = new User();
    private UserService userService = new UserService();
    private File resume;//数据
    private String resumeContype;//mine类型
    private String resumeFileName;//文件名称
    //下载所需参数
    private  String mineType;
    private  String frameFilename;
    private InputStream input;

    public String getMineType() {
        return mineType;
    }

    public String getFrameFilename() {
        return frameFilename;
    }

    public InputStream getInput() {
        return input;
    }

    public void setResume(File resume) {
        this.resume = resume;
    }

    public void setResumeContype(String resumeContype) {
        this.resumeContype = resumeContype;
    }

    //文件名可能是绝对路径,要截取
    public void setResumeFileName(String resumeFileName) {
        int index = resumeFileName.indexOf("\\");
        if (index > 0) {
            this.resumeFileName = resumeFileName.substring(index + 1);
        } else {
            this.resumeFileName = resumeFileName;
        }
    }

    @InputConfig(resultName = "login")
    public String login() {
        //登录功能,
        //得到user对象的用户名和密码,从数据库中查询
        String loginname = user.getLoginname();
        String loginpass = user.getLoginpass();
        //查询出的是新user对象
        User _user = userService.login(loginname, loginpass);
        //如果_user=null说明没有注册或是账号密码错误
        if (_user == null) {
            this.addActionError("用户名或密码错误");
            //返回login页面显示
            return LOGIN;
        }
        return SUCCESS;
    }

    public String findAll() {
        //查询出所有user保存到值栈中,
        ActionContext actionContext = ActionContext.getContext();
        ValueStack valueStack = actionContext.getValueStack();
        valueStack.push(userService.findAll());
        return "list";
    }

    @Override
    public User getModel() {
        return user;
    }

    //添加用户
    public String add() throws IOException {
        //如果用户上传简历了
        if (resume != null) {
            //设置保存的文件名称,显示在页面
            user.setFilename(resumeFileName);

            ServletContext servletContext = ServletActionContext.getServletContext();
            //获取本地保存的绝对路径
            String savepath = servletContext.getRealPath("/WEB-INF/resumes");
            //设置真实名称
            String realpath = CommonUtils.uuid() + "_" + resumeFileName;
            user.setFilepath(realpath);
            //根据文件名称和路径创建目标文件
            File destFile = new File(savepath, user.getFilepath());
            //将目标文件保存到本地中
            FileUtils.copyFile(resume, destFile);
        }
        //设置uuid
        user.setUid(CommonUtils.uuid());
        userService.add(user);

        return "to_list";

    }

    public String query() {
        ActionContext actionContext = ActionContext.getContext();
        ValueStack valueStack = actionContext.getValueStack();
        valueStack.push(userService.query(user));
        return "list";
    }

    public String delete() throws UnsupportedEncodingException {
        userService.delete(user.getUid());
        //删除简历

        //得到简历名称
        String filepath = user.getFilepath();
        if (filepath != null && filepath.trim().isEmpty()) {
            //处理编码问题
            filepath = new String(filepath.getBytes("iso-8859-1"), "utf-8");
            ServletContext servletContext = ServletActionContext.getServletContext();
            //得到保存简历的真实路径
            String savepath = servletContext.getRealPath("/WEB-INF/resumes");
            //创建file对象
            File file = new File(savepath, filepath);
            //删除文件
            file.delete();
        }
        return "to_list";
    }
//编辑之前查询
    public String editView(){
        ActionContext actionContext = ActionContext.getContext();
        ValueStack valueStack = actionContext.getValueStack();
        valueStack.push(userService.load(user.getUid()));
        return "edit";
    }

    public String edit() throws IOException {
        if(resume!=null){//框框里的简历
            //删除老简历
            ServletContext servletContext = ServletActionContext.getServletContext();
            String savepath = servletContext.getRealPath("/WEB/INF");
            File oldFile = new File(savepath,user.getFilepath());
            oldFile.delete();
            //设置user的filename和filepath为新简历内容

            //保存文件名称
            user.setFilename(resumeFileName);
            //设置真实文件名称
            user.setFilepath(CommonUtils.uuid()+"_"+resumeFileName);

            //保存新简历
            File file = new File(savepath,user.getFilepath());
            FileUtils.copyFile(resume,file);

        }
        //修改数据库内容
        userService.edit(user);
        return "to_list";
    }

    //查看用户
    public String load(){
        ActionContext actionContext = ActionContext.getContext();
        ValueStack valueStack = actionContext.getValueStack();
        valueStack.push(userService.load(user.getUid()));
        return "view";
    }

    public String download() throws UnsupportedEncodingException, FileNotFoundException {
        //get请求,处理文件编码问题
        String filename = new String(user.getFilename().getBytes("iso-8859-1"),"utf-8");
        String filepath = new String(user.getFilepath().getBytes("iso-8859-1"),"utf-8");

        ServletContext servletContext = ServletActionContext.getServletContext();
        mineType = servletContext.getMimeType(filename);
        frameFilename = new String(filename.getBytes("gbk"),"iso-8859-1");
        String savepath = servletContext.getRealPath("/WEB/INF");
        File file = new File(savepath,filepath);
        input = new FileInputStream(file);

        return "download";
    }
}
