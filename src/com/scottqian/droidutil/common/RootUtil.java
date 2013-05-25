package com.scottqian.droidutil.common;

import java.io.File;

import com.scottqian.droidutil.common.ShellUtil.ShellCMD;

/**
 * Root帮助类，用于检查是否已经Root
 * @author scott
 *
 */
public class RootUtil {

	/**
	 *  检查机器是否已经Root
	 * @return 是否已经Root
	 */
   public boolean isDeviceRooted() {
       if (checkRootMethod1()){return true;}
       if (checkRootMethod2()){return true;}
       if (checkRootMethod3()){return true;}
       return false;
   }

   /**
    * 	请求Root权限
    */
   public void RequestRoot()
   {
	   ShellUtil.ExecuteCommand(ShellCMD.REQUEST_SU);
   }
   
   private boolean checkRootMethod1(){
       String buildTags = android.os.Build.TAGS;

       if (buildTags != null && buildTags.contains("test-keys")) {
           return true;
       }
       return false;
   }

   private boolean checkRootMethod2(){
       try {
           File file = new File("/system/app/Superuser.apk");
           if (file.exists()) {
               return true;
           }
       } catch (Exception e) { }

       return false;
   }

   private boolean checkRootMethod3() {
       if (ShellUtil.ExecuteCommand(ShellCMD.REQUEST_SU)){
           return true;
       }else{
           return false;
       }
   }
}