package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.shmet.ArithUtil;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.CustomerMapper;
import com.shmet.dao.ProjectMapper;
import com.shmet.dao.UserMapper;
import com.shmet.dao.UserRoleRelMapper;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mysql.gen.Customer;
import com.shmet.entity.mysql.gen.Project;
import com.shmet.entity.mysql.gen.User;
import com.shmet.entity.mysql.gen.UserRoleRel;
import com.shmet.exception.UserAlreadyExistException;
import com.shmet.util.NumberUtil;
import com.shmet.utils.DateUtils;
import com.shmet.vo.CustomerInfoVo;
import com.shmet.vo.CustomerListVo;
import com.shmet.vo.req.CustomerAddReq;
import com.shmet.vo.req.CustomerModifyReq;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class CustomerInfoService {

  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";
  public static final Logger log = LoggerFactory.getLogger(CommonService.class);
  private static final Multimap<String, CustomerListVo> invertIndexMap = HashMultimap.create();

  @Value("${filepath.logo.file-prefix}")
  private String LOGO_PREFIX;
  @Value("${filepath.logo.db-prefix}")
  private String DB_PREFIX;

  @Resource
  private CustomerMapper customerMapper;

  @Resource
  private ProjectMapper projectMapper;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  private LoginService loginService;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Resource
  private UserMapper userMapper;

  @Resource
  private UserRoleRelMapper userRoleRelMapper;

  /**
   * 根据客户编号 查询 客户基础信息
   *
   * @param customerNo 客户编号
   * @return CustomerInfo
   */
  public CustomerInfoVo queryBasicInfo(String customerNo) {
    CustomerInfoVo infoVo = new CustomerInfoVo();
    List<Customer> customers = customerMapper.selectList(new QueryWrapper<Customer>().eq("no", customerNo));
    if (customers.size() > 0) {
      Customer customer = customers.get(0);
      infoVo.setContactName(customer.getContactName());
      infoVo.setMobile(customer.getMobile());

      Project project = Optional.of(
          projectMapper.selectList(
              new QueryWrapper<Project>().eq("customer_id", customer.getCustomerId()))
      ).orElse(Lists.newArrayList()
      ).get(0);
      if (project != null) {
        infoVo.setLatitude(project.getLatitude());
        infoVo.setLongitude(project.getLongitude());
        infoVo.setAddr(project.getAddr());
        Date createDate = project.getCreateDate();
        if (createDate != null) {
          infoVo.setRunDay(
              DateUtils.subDay(
                  createDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()
              )
          );
        }
      }
    }

    Map<String, Double> map = new HashMap<>();
    List<Long> deviceNos;
    if (StringUtils.equalsIgnoreCase("hy", customerNo)) {
      deviceNos = Lists.newArrayList(202040010010003L, 202040010010004L, 202040010010005L, 202040010010006L,
          202040010010007L, 202040010010008L, 202040010010008L, 202040010010010L,
          202040010010011L, 202040010010012L, 202040010010013L);
      for (Long dno : deviceNos) {
        Object o = stringRedisTemplate.opsForHash().get(REALDATA_CACHE, dno + ".QDO");
        if (Objects.nonNull(o)) {
          try {
            String str = objectMapper.writeValueAsString(o);
            RealDataItem item = objectMapper.readValue(str, RealDataItem.class);
            if (item != null && item.getData() != null) {
              double v = Double.parseDouble((String) item.getData());
              if (map.get("powerPlant") == null) {
                map.put("powerPlant", NumberUtil.convert2(v));
              } else {
                map.put("powerPlant", NumberUtil.convert2(ArithUtil.add(map.get("powerPlant"), v)));
              }
            }
          } catch (JsonProcessingException e) {
            log.error("CustomerService queryBasicInfo  " + o + "  转JSON串出错");
          } catch (IOException e) {
            log.error("CustomerService queryBasicInfo JSON串转为 实体Bean 出错");
          }
        }
      }

    } else {
      deviceNos = loginService.getDeviceNos(customerNo);
    }
    infoVo.setPowerPlant(map.get("powerPlant"));
    Pair<Double, Double> pair = deviceService.mutiplyElectricSum(deviceNos);
    infoVo.setDayEpe(pair.getLeft());
    infoVo.setDayEpi(pair.getRight());

    return infoVo;
  }

  /**
   * 客户列表分页查询展示
   */
  public ResultSearch customerPageList(Integer pageNum, Integer pageSize, String customerName) {
    ResultSearch res = new ResultSearch(pageNum, pageSize);

    Page<Customer> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<Customer> queryWrapper = null;
    if (StringUtils.isNotBlank(customerName)) {
      queryWrapper = new LambdaQueryWrapper<Customer>().like(Customer::getCustomerName, customerName);
    }
    IPage<Customer> customerPages = customerMapper.selectPage(page, queryWrapper);

    res.setPageCount(customerPages.getPages());
    res.setTotalCount(customerPages.getTotal());

    List<CustomerListVo> vos = new ArrayList<>();
    for (Customer customer : customerPages.getRecords()) {
      CustomerListVo vo = new CustomerListVo();
      vo.setCustomerId(customer.getCustomerId());
      vo.setCustomerNo(customer.getCustomerNo());

      vo.setCustomerName(customer.getCustomerName());
      vo.setCustomerAdminCount(customer.getAdminAccount() == null ? "" : customer.getAdminAccount());

      User user = userMapper.selectOne(
          new LambdaQueryWrapper<User>().eq(User::getAccount, customer.getAdminAccount())
      );
      if (user != null) {
        vo.setAdminName(user.getName());
      }

      vo.setCompanyLogo(customer.getLogo());

      vos.add(vo);
    }

    res.setData(vos);

    return res;
  }

  //新增客户信息
  @Transactional(rollbackFor = Exception.class)
  public void addCustomerInfo(CustomerAddReq req) {
    //校验 客户编码 跟 管理员账号
    Customer customer = customerMapper.selectOne(
        new LambdaQueryWrapper<Customer>().eq(Customer::getCustomerNo, req.getCustomerNo())
    );
    User user = userMapper.selectOne(
        new LambdaQueryWrapper<User>().eq(User::getAccount, req.getAdminAccount())
    );
    if (customer != null || user != null) {
      throw new UserAlreadyExistException("客户编码或管理员账号已存在,请更换");
    }
    if (!StringUtils.equals(req.getPwd(), req.getCfmPwd())) {
      throw new RuntimeException("密码跟确认密码不匹配");
    }
    //build customer , user , user-role-rel
    Customer c = req.buildCustomer();
    User u = req.buildUser();
    UserRoleRel urr = req.buildUserRoleRel();
    //保存 customer , user , user-role-rel
    customerMapper.insert(c);
    userMapper.insert(u);
    userRoleRelMapper.insert(urr);
  }

  //修改客户信息
  @Transactional(rollbackFor = Exception.class)
  public int modifyCustomerInfo(CustomerModifyReq req) {
    Customer customer = customerMapper.selectOne(
        new LambdaQueryWrapper<Customer>()
            .eq(Customer::getCustomerId, req.getCustomerId())
    );
    if (customer != null) {
      customer.setCustomerName(req.getCustomerName());
      customer.setContactName(req.getAdminName());
      customer.setLogo(req.getLogoUrl());
      return customerMapper.updateById(customer);
    }
    return -1;
  }

  //上传logo
  public String uploadLogo(String module, MultipartFile file) {
    if (file == null) {
      return null;
    }
    String filename = file.getOriginalFilename();
    String filePath = LOGO_PREFIX + module + "/" + filename;
    File dest = new File(filePath);
    if (!dest.getParentFile().exists()) {
      boolean mkdir = dest.getParentFile().mkdir();
      if (mkdir) {
        try {
          file.transferTo(dest);
          return DB_PREFIX + module + "/" + filename;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      try {
        file.transferTo(dest);
        return DB_PREFIX + module + "/" + filename;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 使用倒排索引热加载数据
   *
   * @param customerName 热实时追踪用户名
   * @return List<CustomerListVo>
   */
  public List<CustomerListVo> getCustomerListVos(String customerName) {
    return Lists.newArrayList(invertIndexMap.get(customerName));
  }

  @PostConstruct
  private void createInvertIndex() {
    log.info("load all customerinfo data from db");
    //1.加载所有用户数据
    List<CustomerListVo> customerListVos = queryAllCustomers();
    if (CollectionUtils.isEmpty(customerListVos)) {
      log.error("no customerinfo data in db");
    }
    //创建倒排索引
    for (CustomerListVo listVo : customerListVos) {
      String customerName = listVo.getCustomerName();
      if (StringUtils.isNotBlank(customerName)) {
        List<String> list = splitCustomerNames(customerName);
        for (String key : list) {
          Collection<CustomerListVo> colls = invertIndexMap.get(key);
          if (colls != null && colls.size() > 10) {
            continue;
          }
          invertIndexMap.put(key, listVo);
        }
      }
    }
  }

  private List<String> splitCustomerNames(String customerName) {
    List<String> list = new ArrayList<>();
    int outlength = customerName.length();
    for (int i = 0; i < outlength; i++) {
      for (int j = i + 1; j < outlength + 1; j++) {
        list.add(customerName.substring(i, j));
      }
    }
    return list;
  }

  /**
   * 查询所有客户信息
   *
   * @return List<CustomerListVo>
   */
  private List<CustomerListVo> queryAllCustomers() {
    return CustomerListVo.buildVos(
        customerMapper.selectList(new QueryWrapper<>(null))
    );
  }

  /**
   * 根据版本查询客户项目
   */
  public List<Customer> getCustomerByVersion(String version) {
    return customerMapper.selectList(new QueryWrapper<Customer>().eq("version", version));
  }

}
