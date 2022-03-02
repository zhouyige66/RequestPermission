## 简介
* 一个基于AspectJ实现的注解处理动态权限检查与申请授权的工具库。
## 原理
* 使用AOP的@Around注解，实现在指定的方法调用前实现判断动态权限的授权情况或申请动态权限。
## 使用方法
1. 项目根目录build.gradle（tag为对应的git tag,如1.0.0）
   ```
   repositories {
        ...
        // REMARK 第一步：添加jitpack仓库地址
        maven { url 'https://jitpack.io' }
   }
   dependencies {
        ···
        classpath "com.github.zhouyige66.RequestPermissionExt:permission_ext-plugin:tag"
        // REMARK 第二步：下载repo中jar包，并放在项目根目录下repo下，指定添加AOP Plugin路径
        classpath files('./repo/gradle-android-plugin-aspectjx-2.0.10.jar')
   }
   ```
2. app build.gradle配置（tag为对应的git tag,如1.0.0）
   ```
   plugins {
      ···
      // REMARK 第三步：添加AOP Plugin
      id 'android-aspectjx'
   }
   // REMARK 第四步：按需添加
   aspectjx {
      enabled true
      // 包含以下包，解决"java.util.zip.ZipException: zip file is empty"问题
      include '使用了权限注解的包'
      // exclude 'androidx','kotlin','com.google','com.squareup','com.alipay','org.apache'
   }
   dependencies {
      ···
      // REMARK 第五步:添加依赖
      implementation project(path:":lib")
   }
   ```
3. 应用中使用方式，可参照Demo
   ```
   @PermissionCheck(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
    )
    fun permissionCheck01(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionCheck(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
    )
    fun permissionCheck02(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
    )
    fun permissionApply01(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
    )
    fun permissionApply02(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
        functionDescription = "视频通话",
    )
    fun permissionApply03(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
        functionDescription = "视频通话",
        permissionDeniedPattern = "显示自定义提示：视频通话功能由于%s权限被禁止，无法使用",
    )
    fun permissionApply04(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }
   ```
4. 待完善功能
* [ ] 使用了hujiang的Aspectj插件，修改插件依赖的gradle版本，上传jitpack.io，方便后续使用。
---
## 关于作者
* Email： zhouyige66@163.com
* 有任何建议或者使用中遇到问题都可以给我发邮件。
