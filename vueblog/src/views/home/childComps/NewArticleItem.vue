<template>
	<div>
		<div class="article-main" v-for="(item, index) in blogList" :key="index">
			<div class="article-item">
				<div class="article-title">
					<a href="#">
						<span>
							<router-link :to="{ name: 'BlogDetail', params: { blogId: item.id } }" target="_blank">
								{{ item.title }}
							</router-link>
						</span>
					</a>
				</div>
				<div class="article-content-item">
					<div class="article-img-left" v-if="item.coverImage != null && item.coverImage.trim().length > 10">
						<router-link :to="{ name: 'BlogDetail', params: { blogId: item.id } }" target="_blank">
							<img :src="item.coverImage" alt="" />
						</router-link>
					</div>
					<div class="article-content-right">
						<router-link :to="{ name: 'BlogDetail', params: { blogId: item.id } }" target="_blank">
							<div class="article-content">
								{{ item.description }}
							</div>
						</router-link>
						<div class="article-evaluation">
							<div class="article-good" @click="addLikeNum(item.actionStatus.like, item.id, index)"
								:disabled="loadingStates[item.id]"> <!-- 禁用状态绑定 -->
								<!--登录显示-->
								<img :src="item.actionStatus.like
									? require('../../../assets/img/home/good_active.png')
									: require('../../../assets/img/home/good.png')
									" alt="" />
								{{ item.likeNum }} <span>赞</span>
							</div>
							<div class="article-author">
								作者：<span>{{ item.authorName }}</span>
							</div>
							<div class="article-date">
								发布时间：<span>{{ item.releaseTime }}</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!--infinite-loading这个组件要放在列表的底部，滚动的盒子里面-->
		<infinite-loading ref="infiniteLoading" spinner="spiral" @infinite="infiniteHandler" :distance="200"
			class="infinite-loading-wrap">
			<div slot="spinner">加载中...</div>
			<div slot="no-more">暂无更多数据</div>
			<div slot="no-results">No results Data</div>
			<div slot="error" slot-scope="{ trigger }">
				Error Data, click
				<a href="javascript:;" @click="trigger">here</a> toretry
			</div>
		</infinite-loading>
	</div>
</template>

<script>
import qs from "qs";

import InfiniteLoading from "vue-infinite-loading";

export default {
	name: "",
	data() {
		return {
			page: 1,
			blogList: [],
			blogIdForm: {
				blogId: "",
			},
			isLike: false,
			isShowImg: true,
			loadingStates: {},  // 跟踪每个文章的加载状态
		};
	},
	components: {
		InfiniteLoading,
	},
	mounted() {
	},
	computed: {},
	methods: {
		// 点赞
		async addLikeNum(isLike, id, index) {
			// 如果正在加载，阻止重复点击
			if (this.loadingStates[id]) return;

			// 设置加载状态并保存当前值用于可能的回滚
			this.$set(this.loadingStates, id, true);
			const originalLike = isLike;
			const originalLikeNum = this.blogList[index].likeNum;

			try {
				// 乐观更新：立即切换状态
				this.blogList[index].actionStatus.like = !originalLike;
				this.blogList[index].likeNum += originalLike ? -1 : 1;

				const res = await this.$axios.post(
					"/blog/action/like",
					qs.stringify({ blogId: id }),
					{ headers: { token: localStorage.getItem("token") } }
				);

				if (res.data.code !== 200 || !res.data.status) {
					// 请求失败，回滚状态
					this.blogList[index].actionStatus.like = originalLike;
					this.blogList[index].likeNum = originalLikeNum;
					this.$message.error("操作失败，请重试");
				}
				// 请求成功则保持乐观更新状态
			} catch (error) {
				// 请求异常，回滚状态
				this.blogList[index].actionStatus.like = originalLike;
				this.blogList[index].likeNum = originalLikeNum;
				this.$message.error("网络错误，请检查连接");
			} finally {
				// 清除加载状态
				this.$set(this.loadingStates, id, false);
			}
		},
		// 暴露给父组件调用的方法
		triggerLoad() {
			this.$nextTick(() => {
				if (this.$refs.infiniteLoading) {
					// 重置分页并强制触发加载
					this.page = 1;
					this.blogList = [];
					this.$refs.infiniteLoading.stateChanger.reset();
					this.$refs.infiniteLoading.trigger();
				}
			});
		},
		async infiniteHandler($state) {
			try {
				const res = await this.$axios.get("/blog/list/new?page=" + this.page, {
					headers: { token: localStorage.getItem("token") },
				});

				if (res.data.data.records.length) {
					this.page += 1;

					// 确保 actionStatus 存在，避免 null 引发的 like 访问错误
					res.data.data.records.forEach((item) => {
						if (!item.actionStatus) {
							item.actionStatus = { like: false }; // 默认值，避免 `null`
						}
					});

					this.blogList = [...this.blogList, ...res.data.data.records];
					$state.loaded();
				} else {
					$state.complete();
				}
			} catch (error) {
				console.error("加载最新列表出错", error);
				$state.error();
			}
		},
	},
};
</script>

<style scoped>
.article-main {
	border-radius: 5px;
	margin-right: 15px;
	margin-left: 3px;
	/* border-bottom: 1px solid #f0f0f2; */
	background-color: #ffffff;
	padding: 15px 15px 0 15px;
	margin-bottom: 15px;

	/* display: inline-block; */
	vertical-align: middle;
	-webkit-transform: perspective(1px) translateZ(0);
	transform: perspective(1px) translateZ(0);
	box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
	-webkit-transition-duration: 0.3s;
	transition-duration: 0.3s;
	-webkit-transition-property: box-shadow, transform;
	transition-property: box-shadow, transform;
}

.article-main:hover {
	background-color: #fafafa;

	box-shadow: 0 10px 10px -10px rgba(0, 0, 0, 0.5);
	-webkit-transform: scale(1.02);
	transform: scale(1.02);
}

.article-title a {
	margin-bottom: 10px;
	font-size: 18px;
	font-weight: 700;
	color: #222226;
	overflow: hidden;
	white-space: normal;
	word-break: break-word;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 1;
	line-height: 25px;
}

.article-title a span:hover {
	/*color: pink;*/
	text-decoration: underline;
}

.article-img-left {
	margin-right: 16px;
}

.article-img-left a {
	position: relative;
	border-radius: 2px;
	/*width: 134px;*/
	height: 84px;
	display: inline-block;
	background-position: 50%;
	border: 1px solid #f5f6f7;
	overflow: hidden;
	background: #f5f6f7;
	/*background-image: url("../../../assets/img/home/003.jpg");*/
	background-size: cover;
}

.article-img-left img {
	width: 132px;
	height: 100px;
	object-fit: cover;
}

/*图片文章并排显示*/
.article-content-item {
	display: flex;
	padding-bottom: 15px;
	height: 130px;
}

.article-content-right {
	display: flex;
	flex: 1;
	flex-direction: column;
	justify-content: space-between;
	/*margin-top: 4px;*/
	font-size: 14px;
	font-weight: 400;
	min-height: 6px;
	min-height: 40px;
	color: #555666;
}

.article-content-right .article-content {
	color: #555666;
	font-size: 14px;
	font-weight: 400;
	line-height: 22px;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 4;
	overflow: hidden;
}

/*评价*/
.article-evaluation {
	display: flex;
	align-items: center;
}

.article-evaluation img {
	width: 16px;
	height: 16px;
	margin-right: 3px;
}

.article-evaluation .article-good {
	display: flex;
	align-items: center;
	margin-right: 30px;
}

.article-evaluation .article-author {
	margin-right: 30px;
}
</style>
