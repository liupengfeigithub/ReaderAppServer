package pers.perry.readerapp.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import pers.perry.readerapp.bean.Picture;
import pers.perry.readerapp.service.PictureService;
import pers.perry.readerapp.util.StringUtil;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class PictureAction extends ActionSupport {

	private static final Log log = LogFactory.getLog(PictureAction.class);
	private PictureService service;
	private Picture picture;
	private Integer _id;
	private Integer pageId;

	public PictureAction() {
		this.service = new PictureService();
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public String byIdJson() {
		log.info("byIdJson IP: " + getRemoteIP());
		picture = service.getById(pageId);
		picture.set_readCount(picture.get_readCount() + 1);
		service.update(picture);
		return SUCCESS;
	}

	// 查询list
	public String list() {
		log.info("list IP: " + getRemoteIP());
		// 默认获取10条记录
		Integer maxId = service.getMaxId();
		Integer[] ids = new Integer[10];
		for (Integer id = maxId; id > maxId - 10; id--) {
			ids[id - maxId + 9] = id;
		}
		List<Picture> list = service.getByIds(ids);
		// jsp中使用struts标签库获得数据
		ActionContext.getContext().put("list", list);
		return "list";
	}

	// 编辑
	public String editUI() {
		log.info("editUI IP: " + getRemoteIP());
		picture = service.getById(_id);
		putMapToContext();
		return "editUI";
	}

	public String edit() {
		log.info("edit IP: " + getRemoteIP());
		if (null != picture) {
			if (!service.update(picture)) {
				ActionContext.getContext().put("editERROR", "提交失败.请重试.");
				putMapToContext();
				return "editUI";
			}
		}
		return "toList";
	}

	// 删除
	public String delete() {
		log.info("delete IP: " + getRemoteIP());
		service.delete(_id);
		return "toList";
	}

	// 查看更多
	public String infoUI() {
		log.info("infoUI IP: " + getRemoteIP());
		picture = service.getById(_id);
		return "infoUI";
	}

	// 添加(页面)
	public String addUI() {
		log.info("addUI IP: " + getRemoteIP());
		putMapToContext();
		return "addUI";
	}

	// 添加
	public String add() {
		log.info("add IP: " + getRemoteIP());
		if (null != picture) {
			if (!service.save(picture)) {
				ActionContext.getContext().put("addERROR", "添加失败.请重试.");
			} else {
				// 把数据清空
				picture = null;
				ActionContext.getContext().put("addSUCCESS", "添加成功.");
			}
		} else {
			ActionContext.getContext().put("addERROR", "对象为空,添加失败.请重试.");
		}
		putMapToContext();
		return "addUI";
	}

	private void putMapToContext() {
		Map<String, String> imagesSrc = StringUtil.loadFileName(3);
		// 防止目录中没有的时候,jsp解析出错,所以手动放入
		if (null != picture) {
			imagesSrc.put(picture.get_imageSrc(), picture.get_imageSrc());
		}
		ActionContext.getContext().put("imagesSrc", imagesSrc);
	}

	// 获取远程客户端IP地址
	private String getRemoteIP() {
		return ServletActionContext.getRequest().getRemoteAddr();
	}

}
