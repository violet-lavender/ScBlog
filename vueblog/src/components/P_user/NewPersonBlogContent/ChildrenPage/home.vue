<template>
	<div class="contentright">
		<div class="F-1"
			 v-for="(item) in List"
			 :key="item.id"
			 @click="TurnToShow(item.id)">

			<div class="BlogContent-a">
				<div class="BlogContent-1">{{ item.title }}</div>
				<div class="BlogContent-2">{{ item.description }}</div>
				<div class="BlogContent-3">
					{{ item.releaseTime }}
					<span>{{ item.viewNum }}阅读 .</span>
					<span>{{ item.likeNum }}点赞 .</span>
					<span>{{ item.commentNum }}评论 .</span>
					<span>{{ item.collectionNum }}收藏</span>
				</div>
			</div>
			<div ref="frame" v-if="item.coverImage!=null && item.coverImage.trim().length > 10"
				 class="BlogContent-image">
				<img :src="item.coverImage" >
				<!--				:class="GetSuitableCrop(item.coverImage)"-->
			</div>
		</div>
		<!-- 给下拉刷新预留位置 -->
		<infinite-loading
			ref="infiniteLoading"
			spinner="spiral"
			@infinite="infiniteHandler"
			:distance="200"
			class="infinite-loading-wrap">
			<div slot="spinner">正在玩儿命加载中！！！</div>
			<div slot="no-more">我们也是有底线的^_^</div>
			<div slot="no-results">您目前还没有发布过博客哦，去创作中心试试吧！</div>
			<div slot="error" slot-scope="{ trigger }">
				Error Data, click <a href="javascript:;" @click="trigger">here</a> toretry
			</div>
		</infinite-loading>
	</div>
</template>

<script>
import InfiniteLoading from "vue-infinite-loading";

export default {
	name: 'Home',
	components:{
		InfiniteLoading,
	},
    props:{
		userId: {
            require:true,
            default: 0,
            type:Number,
        },
	},
	data() {
		return {
			//博客列表显示部分
			List: [],
			config: {
				params: {status: 0, page: 1},
				headers: {
					'token': localStorage.getItem('token')
				}
			},
		}
	},
	async created() {
	},
	mounted() {
	},
	watch : {
		userId(newVal, oldVal) {
			if (newVal !== oldVal) {
				// 重置分页和列表
				this.config.params.page = 1;
				this.List = [];
				// 强制重置infinite-loading组件以触发重新加载
				const loader = this.$refs.infiniteLoading;
				if (loader) {
					loader.stateChanger.reset();
				}
			}
		},
	},
	methods: {
		// 底部刷新函数
		async infiniteHandler($state) {
			// 无效userId时不加载
			if (this.userId <= 0) {
				$state.complete();
				return;
			}

			try {
				const res = await this.$axios.get("/blog/console/list", {
					params: {
						userId: this.userId, // 直接使用当前prop值
						status: 1,
						page: this.config.params.page
					},
					headers: this.config.headers
				});

				const records = res.data.data?.records || [];
				if (records.length > 0) {
					this.List = this.List.concat(records);
					this.config.params.page += 1;
					$state.loaded();
				} else {
					$state.complete();
				}
			} catch (error) {
				$state.error();
			}
		},
		// 跳转去显示博客的详情
		TurnToShow(id) {
			var routeUrl = this.$router.resolve({name: 'BlogDetail', params: {blogId: id}})
			window.open(routeUrl.href, '_blank');
		},
		//获取图片宽高
		GetSuitableCrop(imgurl) {
			// 创建实例对象
			let img = new Image()
			// 图片地址
			img.src = imgurl
			// 打印
			console.log("捕获到的元素", this.$refs)
			console.log("this元素", this.imgheight,this.imgwidth)
			return (img.width / this.$refs.frame.offsetWidth) > (img.height / this.$refs.frame.offsetHeight) ? 'imgA' : 'imgB';
		},
	}
}
</script>
<style scoped>
.contentright {
	width: 990px;
	margin: 0 auto;
}

.contentright .F-1 {
	width: 100%;
	min-height: 150px;
	margin-bottom: 20px;
	padding: 16px 24px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	background-color: #ffffff;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	transition: all 0.2s ease-in-out;
	cursor: pointer;
}

.contentright .F-1:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
	background-color: #fafafa;
}

.contentright .F-1 .BlogContent-image {
	width: 160px;
	height: 100px;
	flex-shrink: 0;
	border-radius: 10px;
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f0f0f0;
}

.contentright .F-1 .BlogContent-image img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.3s ease;
	border-radius: 10px;
}

.contentright .F-1:hover .BlogContent-image img {
	transform: scale(1.05);
}

.contentright .F-1 .BlogContent-a {
	flex: 1;
	margin-right: 16px;
	color: #333;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	height: 100px;
	overflow: hidden;
}

.BlogContent-1 {
	font-size: 18px;
	font-weight: 700;
	color: #222;
	line-height: 1.6;
	margin-bottom: 4px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.BlogContent-2 {
	font-size: 14px;
	font-weight: 400;
	color: #555;
	line-height: 1.5;
	margin-bottom: 6px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.BlogContent-3 {
	font-size: 13px;
	color: #888;
	line-height: 1.5;
	margin-top: auto;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.BlogContent-3 span {
	margin-left: 12px;
}

.infinite-loading-wrap {
	margin-top: 20px;
	text-align: center;
	color: #888;
	font-size: 14px;
}
</style>
