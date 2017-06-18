package pers.train.common.base.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pers.train.admin.dao.ArticleMapper;
import pers.train.admin.dao.ArticleTypeMapper;
import pers.train.admin.dao.FriendLinkMapper;
import pers.train.admin.dao.SecurityResourcesMapper;
import pers.train.admin.dao.SecurityRoleMapper;
import pers.train.admin.dao.SecurityUserMapper;
import pers.train.admin.dao.SecurityUserToRoleMapper;
import pers.train.common.base.dao.BaseMapper;
import pers.train.common.base.service.BaseService;




/**
 * 基类Service 用于动态获取泛型中的实体类信息，实现了{@link BaseService}接口<p>
 * 该基类通过实现BaseService接口，然后在其实现方法中调用BaseMapper中的方法实现功能<p>
 * 通过set注入将BaseMapper注入到该基类中<p>
 * 在该基类的构造方法中通过反射获取该基类的泛型对应的实体类，即传入的pojo对象，由于Mybatis的机制，
 * 根据相应的pojo,获取上下文中对应的mapper,然后将此pojo类名首字母小写，再与Mapper字符串拼接，
 * 组成新的xxxMapper。子类必须继承该实现类。<p>
 * 
 * 该类需要注意：
 * <ul>
 *    <li>在该基类中，必须注入所有要用到的xxxMapper，类型为接口，否则报错</li>
 *    <li>其他内容不可乱动</li>
 * </ul>
 * @author mingshan
 *
 * @param <T>
 */
@SuppressWarnings({"unused","rawtypes"})
public class BaseServiceImpl<T> implements BaseService<T> {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private Class clazz=null;
	
    private BaseMapper<T> baseMapper;
 	public void setBaseMapper(BaseMapper<T> baseMapper) {
 		this.baseMapper = baseMapper;
 	}
	
	@Autowired
 	private SecurityUserMapper securityUserMapper;
 	
 	@Autowired
 	private SecurityResourcesMapper securityResourcesMapper;
 	
 	@Autowired
 	private SecurityRoleMapper securityRoleMapper;
 	
 	@Autowired 
 	private SecurityUserToRoleMapper securityUserToRoleMapper;
 	
 	@Autowired
 	private ArticleMapper articleMapper;
 	
 	@Autowired
 	private ArticleTypeMapper articleTypeMapper;
 	
 	@Autowired
 	private FriendLinkMapper friendLinkMapper;
 	
 	
    public  BaseServiceImpl() {
    	 //通过反射机制获取子类传递过来的实体类型信息
    	 ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
    	 clazz = (Class) type.getActualTypeArguments()[0];
    }
     
 	@PostConstruct
	public void init() throws Exception {
		
		// 根据相应的clazz,获取上下文中对应的mapper
		// 1: 获取相应的类名称
		String clazzName = clazz.getSimpleName();	    
		logger.info("Training---clazzName = " + clazzName);
		
		// 2:SecurityUser -> securityUser  -> securityUserMapper
		String clazzDaoName = clazzName.substring(0,1).toLowerCase() + clazzName.substring(1) + "Mapper";//toLowerCase首字母小写
		logger.info("Training---clazzDaoName = " + clazzDaoName);
		
		// 3: 通过clazzDaoName获取相应 Field字段    this.getClass().getSuperclass():获取到相应BaseServiceImpl
		Field daoNameField = this.getClass().getSuperclass().getDeclaredField(clazzDaoName);		
		logger.info("Training---this.getClass().getSuperclass() = "+this.getClass().getSuperclass());
		
		System.out.println("Training---this.getClass().getSuperclass() = "+this.getClass().getSuperclass());
		logger.info("Training---daoNameField = " + daoNameField);
		
		Object object = daoNameField.get(this);		
		logger.info("Training---object = " + object);
		
		// 4: 获取baseMapper 的字段信息
		Field baseDaoNameField = this.getClass().getSuperclass().getDeclaredField("baseMapper");
		baseDaoNameField.set(this, object);
		
	}	
     
	@Override
	public T findById(int id) {

		return baseMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<T> findAll() {

		return baseMapper.selectAll();
	}

	@Override
	public int insert(T t) {

		return baseMapper.insert(t);
	}

	@Override
	public int insertSelective(T t) {

		return baseMapper.insertSelective(t);
	}

	@Override
	public int update(T t) {

		return baseMapper.updateByPrimaryKeySelective(t);
	}

	@Override
	public int delete(Integer id) {

		return baseMapper.deleteByPrimaryKey(id);
	}

	@Override
	public T selectByUniqueFiled(T t) {

		return baseMapper.selectByUniqueFiled(t);
	}

	@Override
	public List<T> findByPage(Map map) {

		return baseMapper.findByPage(map);
	}

}
