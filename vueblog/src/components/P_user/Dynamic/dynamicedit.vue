<template>
	<div class="body">
		<div class="box">
			<div class="boxup">
				<el-input v-model="textarea" :rows="15" class="textedit" maxlength="1000" placeholder="你有什么想说的吗？"
									resize="none" show-word-limit size="medium" type="textarea">
				</el-input>
			</div>
			<div class="boxdown">
				<div class="option">
					<button @click="PublishDynamic()">发布动态</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'Dynamic',
	data() {
		return {
			textarea: "",
			iconList: [{icon: ' icon-tupian', option: '图片', id: 1}],
			// 预览的照片地址
			dialogImageUrl: '',
			dialogVisible: false,
			fileList: [],
			// 底部显示部分
			optionIndex: 1,
			config: {
				// content: '',
				headers: {

					token: localStorage.getItem('token')
				}
			}
		}
	},
	methods: {
		Options(id) {
			switch (id) {
				case 1:
					this.optionIndex = id
					break
			}
		},
		// 发布动态
		PublishDynamic() {
			if (this.textarea.trim() == '') {
				this.$message.error('请编辑内容！');
				return
			}
			let formdata = new FormData()
			formdata.append('content', this.textarea)
			formdata.append('schoolCode', JSON.parse(localStorage.getItem('userMessage')).schoolCode || '')
			//添加院校代码
			// this.config.content = this.textarea;

			this.$axios.post('/blink', formdata, this.config).then(res => {
				// console.log(res)
				this.$router.push('/HeartSay');
				this.$message.success('发布成功');
			})
			// console.log("需要发布的文字部分:",this.textarea)
		},
        // 点击放大照片预览
		handlePictureCardPreview(file) {
			this.dialogImageUrl = file.url;
			this.dialogVisible = true;
		},
		// 点击删除照片
		handleRemove(file, fileList) {
			// 此时已经删除完成
			console.log("删除完成时的的文件列表", fileList);
		},
		// 文件状态改变的时候显示文件列表
		ShowChange(file, fileList) {
			this.fileList = fileList
		},
		// 点击后手动上传动态
		HandleUpload() {
			let fromdata = new fromdata()
			this.fileList.map(item => {
				fromdata.append('', item.raw)
			})
		}
	}
}
</script>

<style scoped>
:root {
	--primary-color: #ff6b9d;
	--secondary-color: #ff9ebc;
	--hover-color: #ffc2d4;
	--text-dark: #5a3a4d;
	--text-light: #8c5c6d;
	--bg-gradient: linear-gradient(135deg, #fff0f4 0%, #ffe9ec 100%);
	--active-color: #ff3b6d;
	--white: #ffffff;
	--white-pinkish: #fff6f8;
}

.body {
	min-height: 100vh;
	background: var(--bg-gradient);
	display: flex;
	justify-content: center;
	align-items: flex-start;
	padding: 60px 30px;
	/* 放大整体布局 */
	font-family: 'Helvetica Neue', sans-serif;
	color: var(--text-dark);
}

.box {
	width: 100%;
	max-width: 1024px;
	/* 稍微增大宽度限制 */
	background: var(--white);
	border-radius: 16px;
	padding: 40px 32px;
	/* 放大内边距 */
	box-shadow: 0 8px 24px rgba(255, 122, 163, 0.12);
	border: 1px solid rgba(255, 122, 163, 0.18);
}

.boxup >>> .textedit {
	font-size: 20px;
	/* 放大字体 */
	font-family: 'Helvetica Neue', sans-serif;
	line-height: 1.8;
	background: #fff0f5;
	border-radius: 12px;
	padding: 24px;
	/* 增加内边距 */
	border: 2px solid transparent;
	transition: all 0.3s ease;
	color: var(--text-light);
}

.boxup >>> .textedit:focus {
	border-color: rgba(255, 107, 157, 0.4);
	background: rgba(255, 235, 238, 0.3);
}

.boxdown {
	width: 100%;
	margin-top: 28px;
	display: flex;
	justify-content: flex-end;
}

.boxdown .option button {
	padding: 14px 40px;
	/* 增大按钮尺寸 */
	background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
	color: var(--white);
	/* 明确指定白色字体 */
	border: none;
	border-radius: 32px;
	font-size: 18px;
	/* 字体更大一些 */
	font-weight: 600;
	cursor: pointer;
	box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
	transition: all 0.3s ease;
}

.boxdown .option button {
	padding: 14px 40px;
	background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
	color: #5a3a4d !important;
	text-shadow: 0 1px 2px rgba(255, 255, 255, 0.3);
	border: none;
	border-radius: 32px;
	font-size: 18px;
	font-weight: 600;
	cursor: pointer;
	box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
	transition: all 0.3s ease;
}

.boxdown .option button:hover {
	transform: translateY(-2px);
	box-shadow: 0 10px 24px rgba(255, 107, 157, 0.4);
	background: linear-gradient(45deg, var(--secondary-color), var(--primary-color));
}

.boxdown .option button:active {
	transform: scale(0.95);
}
</style>



