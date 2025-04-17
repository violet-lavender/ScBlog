<template>
  <div>
    <div class="m-content">
      <div class="top-container">
        <div class="article-bar">
          <!-- <div class="btn-goback"> -->
          <router-link :to="{ name: 'ContentManagement' }" class="btn-goback">
            <i class="el-icon-caret-left"></i>
            <div class="text">文&nbsp;章&nbsp;管&nbsp;理</div>
          </router-link>
          <!-- </div> -->
          <div class="bar-input">
            <input v-model="ruleForm.title" placeholder="请输入文章标题" prop="title" type="text"/>
          </div>
          <div class="btn">
            <button class="btn-save" @click="saveForm()">保存草稿</button>
            <button class="btn-publish" @click="submitForm()">发布文章</button>
					</div>
					<div class="bar-img">
						<a href="#">
							<img :src="avatarUrl" alt="" style="border: 1px solid #e8e8ed"/>
						</a>
					</div>
				</div>
			</div>

			<!-- markdown编辑器  -->
			<mavon-editor ref="md" v-model="ruleForm.content" style="height: calc(100vh - 56px)" @imgAdd="imgAdd"
										@imgDel="imgDel"></mavon-editor>

			<!-- 弹窗 -->
			<div v-if="dialogShow">
				<blog-edit-dialog :blogContent="ruleForm.content" :blogContentHtml="ruleForm.contentHtml" :blogId="ruleForm.id"
													:blogTitle="ruleForm.title" :dialogShow="dialogShow"
													@dialogShowChange="dialogShowChange"></blog-edit-dialog>
			</div>
		</div>
  </div>
</template>

<script>
import BlogEditDialog from "@/views/blogEdit/childComps/BlogEditDialog";
import qs from "qs";

export default {
  name: "",
  components: {
    BlogEditDialog,
  },
  data() {
    return {
      // 目录开始
      tocs: [],
      // 目录结束
      avatarUrl: "",
      ruleForm: {
				id: "",
				title: "",
				description: "",
				content: "",
				contentHtml: "",
				status: 2,
				schoolCode: "",
			},
      dialogShow: false,
    };
  },
  created() {
		// 显示头像
		this.avatarUrl = window.localStorage.avatarUrl;
		this.ruleForm.schoolCode = JSON.parse(localStorage.getItem('userMessage')).schoolCode || '';

		// 获取路由中的 blogId (编辑模式时存在)
		const blogId = this.$route.params.blogId;
		if (blogId) {
			this.loadBlogData(blogId);  // 加载已有博客数据
		}
	},
  methods: {
		// 加载博客
		async loadBlogData(blogId) {
			try {
				const res = await this.$axios.get('/blog/draft', {
					params: {id: blogId},
					headers: {token: localStorage.getItem("token")}
				});

				// 合并接口返回数据到表单
				const draftBlog = res.data.data;
				this.ruleForm = {
					...this.ruleForm,
					id: draftBlog.id,
					title: draftBlog.title,
					content: draftBlog.content,
				};

				// 确保编辑器渲染完成
				this.$nextTick(() => {
					this.$refs.md.d_value = draftBlog.content;
				});
			} catch (error) {
				this.$message.error("加载博客失败");
			}
		},
		// md文档开始
		// 将图片上传到服务器，返回地址替换到md中
		imgAdd(pos, $file) {
			var _this = this;
			var formdata = new FormData();
			formdata.append("file", $file);
			this.$axios
				.post("/blog/console/image", formdata, {
					headers: {token: localStorage.getItem("token")},
				})
				.then((response) => {
					// 第二步.将返回的url替换到文本原位置![...](0) -> ![...](url)
					if (response.status === 200) {
						var url = response.data.data;
						_this.$refs.md.$img2Url(pos, url);
					}
					console.log(response);
				});
		},
		imgDel(pos) {
		},
		// md文档结束
		dialogShowChange(val) {
			this.dialogShow = val;
		},
		// 保存文章
		saveForm() {
			if (!this.$refs.md) {
				this.$message.warning("编辑器尚未初始化完成");
				return;
			}
			// 直接同步编辑器当前渲染内容
			this.ruleForm.contentHtml = this.$refs.md.d_render;

			// 添加验证
			if (!this.validateForm()) return;

			this.$axios
				.post("/blog/console/blog", qs.stringify(this.ruleForm), {
					headers: {token: localStorage.getItem("token")}
				})
				.then((res) => {
					if (res.data.code == 200 && res.data.status == true) {
						this.ruleForm.id = res.data.data; // 更新ID（新建时必需）
						this.$message.success("保存成功~");
					}
				})
				.catch(error => {
					this.$message.error("保存失败：" + error.message);
				});
    },
    // 发布文章
    submitForm() {
      if (this.ruleForm.title.length === 0) {
        this.$message({
          showClose: true,
          message: "请填写标题",
          type: "warning",
        });
      } else if (this.ruleForm.content.length === 0) {
        this.$message({
					showClose: true,
					message: "请填写内容",
					type: "warning",
				});
			} else {
				this.ruleForm.contentHtml = this.$refs.md.d_render;
				// console.log(this.ruleForm.contentHtml)
				this.dialogShow = true;
			}
		},
		validateForm() {
			if (!this.ruleForm.title.trim()) {
				this.$message.warning('标题不能为空');
				return false;
			}
			if (!this.ruleForm.content.trim()) {
				this.$message.warning('内容不能为空');
				return false;
			}
			return true;
		},
	},
};
</script>

<style>
.m-content {
  /*text-align: center;*/
}

/*顶部*/
.top-container {
  width: 100%;
  height: 56px;
  background-color: #f3f3f3;
}

/*.article-bar*/
.article-bar {
  height: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.article-bar .btn-goback {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 24px;
  margin-right: 10px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  color: #6b6b6b;
  /* background-color: pink; */
}

.article-bar .btn-goback .text {
  width: 88px;
}

.article-bar .bar-input {
  width: 100%;
}

.article-bar .bar-input input {
  border: 1px solid #ccc;
  height: 36px;
  width: 100%;
  margin-left: 4px;
  border-radius: 5px;
  padding: 8px;
  padding-left: 16px;
  box-sizing: border-box;
  outline: none;
}

.article-bar .btn {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.article-bar .btn button {
  text-transform: none;
  cursor: pointer;
}

.article-bar .btn .btn-save {
  width: 98px;
  height: 36px;
  margin-right: 16px;
  padding: 0 16px;
  font-size: 16px;
  color: #16a0f8;
  border: 1px solid #16a0f8;
  border-radius: 5px;
  white-space: nowrap;
  background-color: #fff;
  margin-left: 20px;
}

.article-bar .btn .btn-publish {
  width: 98px;
  height: 36px;
  padding: 0 16px;
  font-size: 16px;
  color: #fff;
  border: none;
  border-radius: 5px;
  white-space: nowrap;
  background: #16a0f8;
}

.article-bar .bar-img img {
  height: 38px;
  width: 38px;
  border-radius: 19px;
  border: 1px solid #f0f0f2;
  /*border: 1px solid black;*/
  margin-left: 20px;
  margin-right: 40px;
}

.demo-ruleForm .el-form-item {
  margin-bottom: 0px;
}
</style>
