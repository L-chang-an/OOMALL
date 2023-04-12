// package cn.edu.xmu.oomall.freight.jmeter;
//
//
// import cn.edu.xmu.javaee.core.util.JwtHelper;
// import cn.edu.xmu.oomall.freight.dao.ExpressDao;
// import org.apache.jmeter.config.Arguments;
// import org.apache.jmeter.control.LoopController;
// import org.apache.jmeter.protocol.http.control.Header;
// import org.apache.jmeter.protocol.http.control.HeaderManager;
// import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
// import org.apache.jmeter.protocol.http.util.HTTPArgument;
// import org.apache.jmeter.save.SaveService;
// import org.apache.jmeter.testelement.TestElement;
// import org.apache.jmeter.testelement.TestPlan;
// import org.apache.jmeter.testelement.property.BooleanProperty;
// import org.apache.jmeter.testelement.property.CollectionProperty;
// import org.apache.jmeter.testelement.property.StringProperty;
// import org.apache.jmeter.testelement.property.TestElementProperty;
// import org.apache.jmeter.threads.ThreadGroup;
// import org.apache.jmeter.threads.gui.ThreadGroupGui;
// import org.apache.jmeter.util.JMeterUtils;
// import org.apache.jorphan.collections.ListedHashTree;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.io.*;
// import java.util.ArrayList;
// import java.util.Locale;
//
// // 自动生成指定api的get的jmx文件，由windows的cmd调用生成jtl文件，在收集数据载入csv文件中
// public class GetApiSpeedTest {
//     private static final String APIS[] = {
//             "/shops/2/regions/1/warehouses",
//             "/shops/2/regions/1/warehouses/duizhao"
//     };
//     private static final String jmeterPath = "E:\\newProgram\\apache-jmeter-5.5";
//     private static final String url = "localhost";
//     private static final String port = "8088";
//     private static final String method = "GET";
//     private static final String protocol = "http";
//     static{
//         {
//             // jmeter 前期配置
//             JMeterUtils.setJMeterHome(new File(String.format("%s", jmeterPath)).getAbsolutePath());
//             JMeterUtils.loadJMeterProperties(new File(String.format("%s\\bin\\jmeter.properties", jmeterPath)).getAbsolutePath());
//             JMeterUtils.setProperty("saveservice_properties", String.format("%s\\bin\\saveservice.properties", jmeterPath));
//             JMeterUtils.setProperty("user_properties", String.format("%s\\bin\\user.properties", jmeterPath));
//             JMeterUtils.setProperty("upgrade_properties", String.format("%s\\bin\\upgrade.properties", jmeterPath));
//             JMeterUtils.setProperty("system_properties", String.format("%s\\bin\\system.properties", jmeterPath));
//             JMeterUtils.setProperty("proxy.cert.directory", new File("").getAbsolutePath());
//             JMeterUtils.setLocale(Locale.SIMPLIFIED_CHINESE);
//             JMeterUtils.initLocale();
//         }
//     }
//     private final static Logger logger = LoggerFactory.getLogger(GetApiSpeedTest.class);
//     private static String token = "";
//     private static TestPlan testPlan = new TestPlan("ApiTestPlan");
//     private static ThreadGroup threadGroup = new ThreadGroup();
//     private static  HeaderManager headerManager = new HeaderManager();
//     private static HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
//
//     @BeforeAll
//     private static void init() {
//         JwtHelper jwtHelper = new JwtHelper();
//         token = jwtHelper.createToken(2L, "shop2", 2L, 1, 3600);
//         //TestPlan
//         testPlan.setFunctionalMode(false);
//         testPlan.setSerialized(false);
//         testPlan.setTearDownOnShutdown(true);
//         testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
//         testPlan.setProperty(TestElement.GUI_CLASS,"TestPlanGui");
//         testPlan.setProperty(new BooleanProperty(TestElement.ENABLED, true));
//         testPlan.setProperty(new StringProperty("TestPlan.comments", ""));
//         testPlan.setProperty(new StringProperty("TestPlan.user_define_classpath", ""));
//         Arguments arguments = new Arguments();
//         testPlan.setProperty(new TestElementProperty("TestPlan.user_defined_variables",arguments));
//
//         threadGroup.setDelay(0);
//         threadGroup.setDuration(0);
//         threadGroup.setProperty(new StringProperty(LoopController.LOOPS, "1"));
//         threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, "continue"));
//         threadGroup.setScheduler(false);
//         threadGroup.setName("Group1");
//         threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
//         threadGroup.setProperty(TestElement.GUI_CLASS,"ThreadGroupGui");
//         threadGroup.setProperty(new BooleanProperty(TestElement.ENABLED, true));
//         LoopController lop = new LoopController();
//         lop.setProperty(new StringProperty(LoopController.LOOPS, "1"));
//         lop.setProperty(new BooleanProperty("LoopController.continue_forever", false));
//         threadGroup.setProperty(new TestElementProperty("ThreadGroup.main_controller", lop));
//
//         Header header1 = new Header();
//         header1.setProperty(new StringProperty("Header.name","Content-Type"));
//         header1.setProperty(new StringProperty("Header.value","application/json"));
//         Header header2 = new Header();
//         header2.setProperty(new StringProperty("Header.name","Authorization"));
//         header2.setProperty(new StringProperty("Header.value",token));
//         TestElementProperty HeaderElement1 = new TestElementProperty("",header1);
//         TestElementProperty HeaderElement2 = new TestElementProperty("",header2);
//         ArrayList<TestElementProperty> list2 = new ArrayList<>();
//         list2.add(HeaderElement1);
//         list2.add(HeaderElement2);
//         headerManager.setProperty(new CollectionProperty("HeaderManager.headers",list2));
//         headerManager.setProperty(new StringProperty("TestElement.test_class","org.apache.jmeter.protocol.http.control.HeaderManager"));
//         headerManager.setProperty(new StringProperty("TestElement.name","HTTP Header Manager"));
//         headerManager.setProperty(new StringProperty("TestElement.enabled","true"));
//         headerManager.setProperty(new StringProperty("TestElement.gui_class","org.apache.jmeter.protocol.http.gui.HeaderPanel"));
//
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.domain", url));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.port", port));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.protocol", protocol));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.method", method));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.contentEncoding", "UTF-8"));
//         httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.follow_redirects", true));
//         httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.use_keepalive", true));
//         httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.DO_MULTIPART_POST", false));
//         httpSamplerProxy.setProperty(new StringProperty("TestElement.gui_class", "org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui"));
//         httpSamplerProxy.setProperty(new StringProperty("TestElement.test_class", "org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy"));
//         httpSamplerProxy.setProperty(new StringProperty("TestElement.name", "HTTP Request"));
//         httpSamplerProxy.setProperty(new StringProperty("TestElement.enabled", "true"));
//         httpSamplerProxy.setProperty(new BooleanProperty("HTTPSampler.postBodyRaw", true));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.embedded_url_re", ""));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.connect_timeout", ""));
//         httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.response_timeout", ""));
//     }
//     @Test
//     public  void testCmd() throws  IOException {
//         threadGroup.setNumThreads(1400);
//         threadGroup.setRampUp(10);
//         int t = 0;
//         for (String API: APIS) {
//             httpSamplerProxy.setProperty(new StringProperty("HTTPSampler.path", API));
//             ListedHashTree hashTreeResultCollectorAndHeaderManager = new ListedHashTree();
//             hashTreeResultCollectorAndHeaderManager.add(headerManager);
//             ListedHashTree hashTreeHTTPSamplerProxy = new ListedHashTree();
//             hashTreeHTTPSamplerProxy.add(httpSamplerProxy, hashTreeResultCollectorAndHeaderManager);
//             ListedHashTree hashTreeThreadGroup = new ListedHashTree();
//             hashTreeThreadGroup.add(threadGroup, hashTreeHTTPSamplerProxy);
//             ListedHashTree hashTreeTestPlan = new ListedHashTree();
//             hashTreeTestPlan.add(testPlan, hashTreeThreadGroup);
//             try {
//                 SaveService.saveTree(hashTreeTestPlan, new FileOutputStream(String.format(".\\test%d.jmx", t)));
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             }
//             logger.info("\nRun Api: {}", API);
//             String command = String.format("%s\\bin\\jmeter -n -t .\\test%d.jmx  -l .\\test%d.jtl", jmeterPath, t, t);
//             try {
//                 Process p = Runtime.getRuntime().exec("cmd.exe /C start /b " + command);
//                 p.waitFor();
//                 Thread.currentThread().sleep(1000);
//                 Runtime.getRuntime().exec("cmd.exe /C start /b wmic process where name='cmd.exe' call terminate");
//                 BufferedReader bufrIn = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
//                 StringBuilder result = new StringBuilder();
//                 String line = null;
//                 while ((line = bufrIn.readLine()) != null) {
//                     result.append(line).append('\n');
//                 }
//             } catch (InterruptedException e) {
//                 throw new RuntimeException(e);
//             }
//             try {
//                 Process p = Runtime.getRuntime().exec(String.format("E:\\oomall\\freight\\qaq.bat %d", t++));
//                 p.waitFor();
//                 BufferedReader bufrIn = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
//                 StringBuilder result = new StringBuilder();
//                 String line = null;
//                 while ((line = bufrIn.readLine()) != null) {
//                     result.append(line).append('\n');
//                 }
//                 logger.info("\nFinish Api: Api={}, {}", API, result);
//             } catch (InterruptedException e) {
//                 throw new RuntimeException(e);
//             }
//         }
//     }
// }
