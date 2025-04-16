<template>
	<div class="upload-container">
		<el-upload ref="upload" :action="uploadImgUrl" :auto-upload="autoUpload"
							 :before-upload="handleBeforeUpload" :class="{ hide: fileList.length > 0 }" :limit="limit"
							 :on-change="upData"
							 :on-error="handleUploadError" :on-exceed="handleExceed" :on-preview="handlePictureCardPreview"
							 :on-remove="handleRemove"
							 :on-success="handleUploadSuccess" :show-file-list="true" class="avatar-uploader"
							 list-type="picture-card" name="scrm-files">
			<i class="el-icon-plus"></i>
		</el-upload>

		<el-button class="upload-btn" size="medium" type="primary" @click="submitUpload">
			上传到服务器
		</el-button>

		<!-- 图片预览弹窗 -->
		<el-dialog :visible.sync="dialogVisible" width="40%">
			<img :src="dialogImageUrl" alt="预览图" style="width: 100%"/>
		</el-dialog>
	</div>
</template>

<script>
export default {
	data() {
		return {
			limit: 1,           // 最大上传数量
			fileSize: 5,        // 上传图片的最大限制（MB）
			uploadImgUrl: 'https://localhost:8081/user/avatar',  // 上传图片的接口地址
			fileType: ["png", "jpg", "jpeg"], // 上传类型
			fileList: [],
			autoUpload: false,
			config: {headers: {'Content-Type': 'multipart/form-data', 'token': localStorage.getItem('token')}},
			formdata: "",
			dialogVisible: false,
			dialogImageUrl: ""
		};
	},
	methods: {
		// 上传成功回调
		handleUploadSuccess(res) {
			this.fileList.push({name: res.fileName, url: res.data});
			this.loading.close();
		},
		// 上传前loading加载,判断所上传照片是否合乎规范
		handleBeforeUpload(file) {
			let isImg = false;
			if (this.fileType.length) {
				let fileExtension = "";
				if (file.name.lastIndexOf(".") > -1) {
					fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
				}
				isImg = this.fileType.some((type) => {
					if (file.type.indexOf(type) > -1) return true;
					if (fileExtension && fileExtension.indexOf(type) > -1) return true;
					return false;
				});
			} else {
				isImg = file.type.indexOf("image") > -1;
			}
			if (!isImg) {
				this.$message.error(`文件格式不正确, 请上传${this.fileType.join("/")}图片格式文件!`);
				return false;
			}
			if (this.fileSize) {
				const isLt = file.size / 1024 / 1024 < this.fileSize;
				if (!isLt) {
					this.$message.error(`上传头像图片大小不能超过 ${this.fileSize} MB!`);
					return false;
				}
			}
		},
		// 更新上传的数据
		upData(file, fileList) {
			let formdata = new FormData();
			fileList.map(item => { // fileList本来就是数组，就不用转为真数组了
				formdata.append("avatarFile", item.raw);  // 将每一个文件图片都加进formdata
			});
			this.formdata = formdata;
		},
		// 提交上传
		submitUpload() {
			this.$axios.put("/user/avatar", this.formdata, this.config)
				.then(res => {
					if (res.data.status == true) {
						this.$message({
							message: '头像修改成功',
							type: 'success'
						});
						this.bus.$emit('refreshUserInfo');
					} else {
						this.$message({
							message: '头像修改失败',
							type: 'error'
						});
					}
				});
		},
		// 上传失败
		handleUploadError() {
			this.$message({
				type: "error",
				message: "上传失败",
			});
			this.loading.close();
		},
		// 文件个数超出
		handleExceed() {
			this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
		},
		// 删除图片
		handleRemove() {
			this.fileList = [];
		},
		// 图片预览
		handlePictureCardPreview(file) {
			this.dialogImageUrl = file.url;
			this.dialogVisible = true;
		},
		handleClose(done) {
			this.$confirm('确认关闭？')
				.then(_ => {
					done();
				})
				.catch(_ => {
				});
		}
	}
}
</script>

<style scoped>
.upload-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30px 20px;
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.avatar-uploader {
	margin-bottom: 16px;
	border-radius: 10px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.upload-btn {
	margin-top: 8px;
	border-radius: 8px;
}

::v-deep.hide .el-upload--picture-card {
	display: none;
}
</style>
