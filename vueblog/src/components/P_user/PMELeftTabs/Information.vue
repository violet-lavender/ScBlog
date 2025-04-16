<template>
  <div class="rightContent">
    <div class="rightContentA shadow-card">
			<div class="rightContentA-1">
				<div class="rightContentA-1-container">
					<div class="img-wrapper" @click="ShowChose">
						<img :src="this.$parent.userMessage.avatarUrl" alt="" class="avatar">
						<img class="img-hover" :src="hoverCamera" alt="">
					</div>
					<div class="name">
						<span>{{ this.$parent.userMessage.nickname }}</span>
					</div>
				</div>
			</div>
		</div>

		<div class="rightContentB shadow-card">
			<div class="rightContentB-1">
				基本信息
				<button v-if="buttonSeen" class="button" @click="EditMessage()">编辑</button>
			</div>
			<div class="rightContentB-2">
				<div class="rightContentB-2-form">
					<ul>
						<li>
							<span v-if="formchange">用户昵称：{{ this.$parent.userMessage.nickname }}</span>
							<div v-else>
								<el-form :model="form" class="inline-form" label-width="80px">
									<el-form-item label="用户昵称">
										<div class="form-row">
											<el-input v-model="form.nickname" class="short-input"/>
											<el-button class="ml-2" type="primary" @click="submit">确定</el-button>
											<el-button class="ml-1" @click="cancel">取消</el-button>
										</div>
									</el-form-item>
								</el-form>
							</div>
						</li>
            <li><span>用户ID：{{ this.$parent.userMessage.username }}</span></li>
						<li>
							<span v-if="formchange">个人简介：{{ this.$parent.userMessage.intro }}</span>
							<div v-else>
								<el-form :model="form" class="inline-form" label-width="80px">
									<el-form-item label="个人简介">
										<div class="form-row">
											<el-input v-model="form.intro" :rows="2" class="short-input" style="resize: none;"
																type="textarea"/>
											<div class="button-group">
												<el-button class="ml-2" type="primary" @click="submitIntro">确定</el-button>
												<el-button class="ml-1" @click="cancel">取消</el-button>
											</div>
										</div>
									</el-form-item>
								</el-form>
							</div>
						</li>
          </ul>
        </div>
      </div>
    </div>

    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
      <uploadImg class="PickerImg"></uploadImg>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="GetUserData">确 定</el-button>
    </el-dialog>
  </div>
</template>

<script>
import uploadImg from "@/components/P_user/UploadImg/uploadImg.vue";

export default {
	name: "Information",
	components: {uploadImg},
	data() {
		return {
			username: "",
			mymoney: 0,
			buttonSeen: true,
			formchange: true,
			hoverCamera: "https://scblogs-avatar.obs.cn-north-4.myhuaweicloud.com/hover_amera.png",
			dialogVisible: false,
			userMessage: {},
			form: {
        username: "",
        nickname: "",
        avatarUrl: "https://scblogs-avatar.obs.cn-north-4.myhuaweicloud.com/default_avatar.jpg",
        registerTime: "",
        intro: ""
      },
      config: {
        headers: {
					token: localStorage.getItem("token")
        }
      }
    };
  },
  created() {
		let pMountedTimer = window.setInterval(() => {
			if (window.parentMounted) {
				window.clearInterval(pMountedTimer);
				this.GetData();
			}
		}, 100);
  },
  methods: {
    EditMessage() {
			this.buttonSeen = false;
			this.formchange = false;
		},
    GetData() {
      this.form = JSON.parse(JSON.stringify(this.$parent.userMessage));
    },
    ShowChose() {
			this.dialogVisible = true;
    },
    handleClose(done) {
			this.$confirm("确认关闭？").then(() => done()).catch(() => {
			});
    },
    GetUserData() {
			this.dialogVisible = false;
    },
    submit() {
			let formdata = new FormData();
			formdata.append("nickname", this.form.nickname);
			this.$axios.put("/user/nickname", formdata, this.config).then(res => {
				if (res.data.status === true) {
					this.$message({message: "用户昵称修改成功", type: "success"});
					this.$parent.GetData();
					this.formchange = true;
					this.buttonSeen = true;
				} else {
					this.$message({message: "修改失败", type: "error"});
				}
			});
		},
    submitIntro() {
      let formdata = new FormData();
      formdata.append("intro", this.form.intro);
			this.$axios.put("/user/intro", formdata, this.config).then(res => {
				if (res.data.status === true) {
					this.$message({message: "个人简介修改成功", type: "success"});
					this.$parent.GetData();
					this.formchange = true;
					this.buttonSeen = true;
				} else {
					this.$message({message: "修改失败", type: "error"});
				}
			});
		},
		cancel() {
			this.formchange = true;
			this.buttonSeen = true;
		}
	}
};
</script>

<style scoped lang="less">
.PickerImg {
	margin: 0 20px 30px 10px;
}

.rightContent {
	width: 100%;
	background: transparent;
}

/* 阴影样式 */
.shadow-card {
	background: white;
	border-radius: 12px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
	margin-bottom: 24px;
}

.rightContentA-1-container {
	padding: 32px;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.img-wrapper {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	overflow: hidden;
	position: relative;
	cursor: pointer;
	margin-bottom: 12px;
}

.avatar {
	width: 100%;
	height: 100%;
	object-fit: cover;
	display: block;
}

.img-hover {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	display: none;
	z-index: 2;
}

.img-wrapper:hover .img-hover {
	display: block;
}

.name {
	font-size: 1.2rem;
	font-weight: 600;
	color: #333;
}

.rightContentB-1 {
	height: 80px;
	line-height: 80px;
	padding-left: 40px;
	font-size: 1.2rem;
	font-weight: 800;
	border-bottom: 1px solid #f5f6f7;
	position: relative;
}

.button {
	position: absolute;
	right: 100px;
	top: 20px;
	height: 36px;
	padding: 0 20px;
	border-radius: 8px;
	font-size: 1rem;
	color: white;
	background: #16a0f8;
	border: none;
	cursor: pointer;

	&:hover {
		box-shadow: 2px 0px 5px grey, -2px 0px 5px grey;
	}
}

.rightContentB-2 {
	padding: 20px 40px;
}

.rightContentB-2-form ul {
	list-style: none;
	padding: 0;
}

.rightContentB-2-form ul li {
	margin-bottom: 30px;
	font-size: 1rem;
}

/* 表单样式优化 */
.inline-form .form-row {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
}

.short-input {
	max-width: 300px;
	flex: 1;
}

.ml-1 {
	margin-left: 8px;
}

.ml-2 {
	margin-left: 12px;
}

.button-group {
	display: flex;
	gap: 8px;
	margin-top: 8px;
}
</style>
