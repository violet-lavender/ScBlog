<template>
  <div>
    <div v-if="contentSeen" class="rightContent">
      <!--    <div class="rightContentA">-->
      <!--    </div>-->
      <div class="rightContentB">
        <div class="rightContentB-1">
          账号设置
        </div>
        <div class="rightContentB-2">
          <ul>
            <li>
              <div class="password">
                <div class="text">密码</div>
                <div class="right">
                  <span></span>
                  <span @click="ChangePassword">修改密码</span>
                </div>
              </div>
            </li>
            <li>
              <div class="e-mail">
                <div class="text">邮箱</div>
                <div class="right">
                  <span>{{ this.$parent.userMessage.mail }}</span>
                  <span @click="ChangeEmail">修改邮箱</span>
                </div>
              </div>
            </li>
            <li>
              <div class="deleteUser">
                <div class="text">账号注销</div>
                <div class="delete">
                  <span @click="DeleteAccount">立即注销</span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <!-- 修改密码模块 -->
    <div v-if="changepassword" class="changepass">
      <div class="changepasscontent">
        <div class="changepassB">
          <span @click="Back()">账号设置</span>/
          <span @click="changePassSeen = true">修改密码</span>
        </div>
        <div v-if="!changePassSeen" class="changepassC">
          账号修改完成！
        </div>
        <div v-if="changePassSeen" class="changepassA">
          <el-form :model="ruleForm2" status-icon :rules="rules2" ref="ruleForm2" label-width="100px"
            class="demo-ruleForm">
            <el-form-item label="输入旧密码" prop="oldpass">
              <el-input type="password" v-model="ruleForm2.oldpass" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="输入新密码" prop="pass">
              <el-input type="password" v-model="ruleForm2.pass" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="checkPass">
              <el-input type="password" v-model="ruleForm2.checkPass" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm('ruleForm2')">确认修改</el-button>
              <el-button @click="resetForm('ruleForm2')">重置</el-button>
            </el-form-item>
          </el-form>
          <div class="errorTip" v-if="tipSeen">密码输入错误,请重试</div>
        </div>
      </div>
    </div>
    <!-- 修改邮箱模块 -->
    <div v-if="changeemail" class="changeemail">
      <div class="changeemailcontent">
        <div class="changepassB">
          <span @click="Back()">账号设置</span>/
          <span @click="changeEmailSeen = true">修改邮箱</span>
        </div>
        <div v-if="!changeEmailSeen" class="changeemailC">
          邮箱修改完成！
        </div>
        <div v-if="changeEmailSeen" class="changeemailA">
          <el-form :model="ruleForm3" status-icon :rules="rules3" ref="ruleForm3" label-width="100px"
            class="demo-ruleForm">
            <el-form-item label="输入新邮箱" prop="mail">
              <el-input v-model="ruleForm3.mail" autocomplete="off" class="form-item"></el-input>
            </el-form-item>
            <el-form-item label="输入验证码" prop="mailVerify">
              <div class="code-container">
                <el-input v-model="ruleForm3.mailVerify" placeholder="请输入验证码" style="width: 250px; margin-right: 10px">
                </el-input>
                <el-button type="danger" @click="obtainEmailCode" style="width: 120px" :disabled="!isShowTime">
                  <span v-show="isShowTime">获取验证码</span>
                  <span v-show="!isShowTime">{{ count }} s</span>
                </el-button>
              </div>
            </el-form-item>
            <el-form-item class="form-buttons">
              <el-button type="primary" @click="submitEmailForm('ruleForm3')" class="form-btn">确认修改</el-button>
              <el-button @click="resetForm('ruleForm3')" class="form-btn">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    <!-- 注销账户模块 -->
    <div v-if="deleteaccount" class="deleteAccount">
      <div class="deletecontent">
        <div class="changepassB">
          <span @click="Back()">账号设置</span>/
          <span>修改密码</span>
        </div>
        <div class="deleteaccountA">
          确定注销吗？该行为无法撤销，请谨慎操作！
        </div>
        <div class="button">
          <el-form :model="ruleForm2" status-icon :rules="rules2" ref="ruleForm2" label-width="100px"
            class="demo-ruleForm">
            <el-form-item label="输入密码" prop="pass">
              <el-input type="password" v-model="ruleForm2.pass" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="checkPass">
              <el-input type="password" v-model="ruleForm2.checkPass" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="DeleteTrue('ruleForm2')">狠心注销</el-button>
              <el-button @click="resetForm('ruleForm2')">取消注销</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "TabsContent2",
  components: {
  },
  data() {
    var checkOldpass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入旧密码'));
      } else {
        callback();
      }
    };
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'));
      } else {
        if (this.ruleForm2.checkPass !== '') {
          this.$refs.ruleForm2.validateField('checkPass');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.ruleForm2.pass) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      // 修改密码
      changepassword: false,
      deleteaccount: false,
      contentSeen: true,
      // 用户修改完成没有
      changePassSeen: true,
      // 改昵称时返回错误弹出提示
      tipSeen: false,
      // 修改邮箱的状态
      changeemail: false,
      contentSeen: true,
      changeEmailSeen: true,
      ruleForm2: {
        oldpass: '',
        pass: '',
        checkPass: '',
      },
      rules2: {
        oldpass: [
          { validator: checkOldpass, trigger: 'blur' }
        ],
        pass: [
          { validator: validatePass, trigger: 'blur' }
        ],
        checkPass: [
          { validator: validatePass2, trigger: 'blur' }
        ],

      },
      ruleForm3: {
        mail: '',
        mailVerify: ''
      },
      rules3: {
        mail: [
          { required: true, message: '请输入新邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
        ],
        mailVerify: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      },
      config: { headers: { 'Content-Type': 'multipart/form-data', 'token': localStorage.getItem('token') } },
      isShowTime: true, // 是否显示验证码按钮
      count: "", // 验证码倒计时
      timer: null // 定时器
    };
  },
  mounted() {
    this.GetData()
  },
  methods: {
    //   提交修改密码
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(valid, formName)
          // alert('submit!');
          // 开始调用接口修改用户密码
          let formdata = new FormData()
          formdata.append("oldPassword", this.ruleForm2.oldpass)
          formdata.append("newPassword", this.ruleForm2.checkPass)
          console.log("formdata", formdata)
          this.$axios.put('/user/password', formdata, this.config).then(res => {
            console.log("修改昵称接口的返回值", res)
            if (res.data.status === true) {
              this.$message({
                message: '密码修改成功',
                type: 'success'
              });
              // 重置表单
              this.resetForm('ruleForm2')
              this.changePassSeen = false
              this.tipSeen = false
            } else {
              this.$message({
                message: '密码修改失败',
                type: 'error'
              });
              this.tipSeen = true
              this.resetForm('ruleForm2')
            }
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    // 请求数据放入本地浏览器
    GetData() {
      this.form = this.$parent.userMessage
      // console.log("获取浏览器全局数据",this.$parent.userMessage)
      console.log("获取浏览器全局数据", this.form)
    },
    ChangePassword() {
      this.contentSeen = false
      this.changepassword = true
      // 点击成功后的页面隐藏
      this.changePassSeen = true
      // 出错后的提示语隐藏
      this.tipSeen = false
    },
    // 点击修改邮箱按钮
    ChangeEmail() {
      this.contentSeen = false;
      this.changeemail = true;
      this.changeEmailSeen = true;
    },
    // 返回账号设置
    Back() {
			this.changepassword = false
			this.changeemail = false
			this.deleteaccount = false
			this.contentSeen = true
		},
    // 获取邮箱验证码
    obtainEmailCode() {
      if (this.ruleForm3.mail === "") {
        this.$message({
          message: "请输入邮箱地址",
          type: "warning"
        });
        return;
      }
      // 发送请求获取验证码
      let formdata = new FormData();
      formdata.append("mail", this.ruleForm3.mail);
      this.$axios
        .post('/user/mail/send-mail-verify', formdata, this.config)
        .then((res) => {
          if (res.data) {
            this.$message({
              message: "验证码已发送至邮箱，请查收",
              type: "success"
            });
            this.startTimer();
          } else {
            this.$message({
              message: "发送验证码失败，请稍后再试",
              type: "error"
            });
          }
        });
    },
    // 启动倒计时
    startTimer() {
      if (!this.timer) {
        this.count = 90;
        this.isShowTime = false;
        this.timer = setInterval(() => {
          if (this.count > 0 && this.count <= 90) {
            this.count--;
          } else {
            this.isShowTime = true;
            clearInterval(this.timer);
            this.timer = null;
          }
        }, 1000);
      }
    },
    // 提交修改邮箱表单
    submitEmailForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let formdata = new FormData();
          formdata.append("mail", this.ruleForm3.mail);
          formdata.append("mailVerify", this.ruleForm3.mailVerify);
          this.$axios
            .put('/user/mail', formdata, this.config)
            .then((res) => {
              if (res.data.status) {
                this.$message({
                  message: '邮箱修改成功',
                  type: 'success'
                });
                this.changeEmailSeen = false;
              } else {
                this.$message({
                  message: '修改失败，请检查验证码',
                  type: 'error'
                });
              }
            });
        }
      });
    },
    // 重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    // 跳转至注销账号
    DeleteAccount() {
      this.contentSeen = false
      this.deleteaccount = true
    },
    // 确认开始注销账号
    DeleteTrue(formName) {
      // 调用接口开始注销账号
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(valid, formName)
          // alert('submit!');
          // 开始调用接口修改用户密码
          let formdata = new FormData()
          formdata.append("password", this.ruleForm2.pass)
          console.log("标头headerstoken", this.config)
          this.$axios.delete('/user/user', { data: formdata, headers: { 'token': localStorage.getItem('token') } }).then(res => {
            if (res.data.data.status === true) {
              this.$message({
                message: '注销成功',
                type: 'success'
              });
            } else {
              this.$message({
                message: '注销失败',
                type: 'error'
              });
              // 注销之后退出账号
            }
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    }
  }
}
</script>
<style scoped>
/* 修改邮箱模块样式 */
.changeemailA {
	width: 60%;
	margin: 0 auto;
	padding: 20px;
	border-radius: 8px;
	background-color: #f9f9f9;
}

.changeemailcontent {
  width: calc(100% - 40px);
  height: 100%;
  margin: 0 20px;
}

.changeemailA .form-item {
  width: 100%;
}

/* 统一表单项样式 */
.changeemailA .el-form-item {
  margin-bottom: 22px;
}

/* 标签对齐设置 */
.changeemailA .el-form-item__label {
	width: 120px;
	text-align: right;
	padding-right: 20px;
	font-size: 16px;
	color: #333;
}

/* 内容区域对齐 */
.changeemailA .el-form-item__content {
	margin-left: 120px !important;
	line-height: normal;
}

/* 验证码容器布局 */
.code-container {
  display: flex;
  align-items: center;
}

/* 输入框统一宽度 */
.changeemailA .el-input {
  width: 348px;
}

/* 验证码输入框特殊宽度 */
.changeemailA .code-container .el-input {
  width: 250px;
}

/* 按钮间距调整 */
.changeemailA .el-button {
  margin-left: 10px;
}

/* 右侧内容部分 */
.rightContent {
  width: 100%;
  background: transparent;
}

.rightContentA {
	width: 100%;
	background: linear-gradient(to bottom right, rgb(233, 189, 189), rgb(255, 255, 255));
	height: 230px;
	border-radius: 8px;
}

.rightContentB {
	width: 100%;
	height: 600px;
	background: white;
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rightContentB-1 {
	width: 100%;
	height: 100px;
	line-height: 100px;
	font-size: 30px;
	font-weight: 800;
	margin-left: 30px;
	color: #333;
}

.rightContentB-2 {
	width: calc(100% - 60px);
	margin: 0px 30px;
}

.rightContentB-2 ul {
  width: 100%;
  list-style: none;
}

.rightContentB-2 ul li {
	width: 100%;
	height: 80px;
	line-height: 80px;
	margin: 4px 0;
	/* 增加垂直间距控制 */
	font-size: 20px;
	font-weight: 600;
	border-bottom: 1px solid #eee;
}

/* 给最后一个列表项添加顶部间距 */
.rightContentB-2 ul li:last-child {
	margin-top: 30px;
	border-top: 1px solid #eee; /* 增加分割线提升视觉效果 */
}

.rightContentB-2 ul li .text {
	width: 80px;
	float: left;
	color: #666;
}

.rightContentB-2 ul li .right {
	float: right;
}

.rightContentB-2 ul li .delete {
  float: right;
  color: red;
	cursor: pointer;
}

.rightContentB-2 ul li .right span:nth-child(1) {
	margin-right: 50px;
}

.rightContentB-2 ul li .right span:nth-child(2) {
	cursor: pointer;
	color: rgb(21, 123, 201);
	transition: color 0.3s;
}

.rightContentB-2 ul li .right span:nth-child(2):hover {
	color: rgb(252, 85, 49);
}

/* 修改密码模块样式 */
.changepass {
	width: 100%;
	height: 500px;
	background: white;
	border-radius: 8px;
}

.changepass .changepasscontent {
	width: calc(100% - 40px);
	height: 100%;
	margin: 0 20px;
}

.changepassB {
  width: 100%;
  height: 50px;
  line-height: 50px;
  font-size: 18px;
  color: grey;
}

.changepassB span:hover {
  color: rgb(252, 85, 49);
}

.changepass .changepasscontent .changepassA {
	width: 50%;
	height: calc(100% - 50px);
	margin: 100px auto;
}

.changepass .changepasscontent .changepassA .errorTip {
  width: 100%;
  height: 20%;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  color: rgb(234, 50, 50);
}

.changepass .changepasscontent .changepassC {
	width: 50%;
	height: calc(100% - 50px);
	margin: 100px auto;
	text-align: center;
	font-size: 40px;
	font-weight: 600;
	color: #333;
}

/* 注销账户模块样式 */
.deleteAccount {
	width: 100%;
	height: 500px;
	background: white;
	border-radius: 8px;
}

.deletecontent {
	width: calc(100% - 40px);
	height: 100%;
	margin: 0 20px;
}

.deleteaccountA {
	width: 100%;
	height: 200px;
	line-height: 200px;
	text-align: center;
	font-size: 40px;
	font-weight: 800;
	color: #333;
}

.deletecontent .button {
  width: 40%;
  height: 200px;
  margin: 0 auto;
  line-height: 200px;
}

/* 表单按钮样式统一 */
.demo-ruleForm .el-form-item {
  margin-bottom: 20px;
}

/* 验证码按钮样式 */
.verifyCodeItem {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.getCodeButton {
	margin-left: 10px;
	width: 120px;
	height: 36px;
	font-size: 14px;
	color: white;
	background-color: #409eff;
	border-radius: 4px;
}

.getCodeButton:disabled {
  background-color: #dcdfe6;
  color: #c0c4cc;
}
</style>
