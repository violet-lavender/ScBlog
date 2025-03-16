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
						status: 0,
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
	/* overflow: hidden; */
}

.contentright .F-1 {
	width: 100%;
	height: 150px;
	margin-bottom: 20px;
	border-radius: 5px;
	padding: 10px 20px;
	display: flex;
	align-items: center;
	background: white;
	transition: all 0.15s linear;
	cursor: pointer;
	overflow: hidden;
}

.contentright .F-1:hover {
	transform: scale(1.01);
	background: rgb(247, 247, 247);
}

.contentright .F-1 .BlogContent-image {
	width: 170px;
	height: 120px;
	border-radius: 10px;
	overflow: hidden;
	display: flex;
	box-sizing: border-box;
}
.contentright .F-1 .BlogContent-image img {
	width: 100%;
	/*height: auto;*/
	object-fit: cover;
}
.contentright .F-1 .BlogContent-image imgB {
	height: 100%;
}
.contentright .F-1 .BlogContent-a {
	width: 50rem;
	height: 120px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: normal;
	color: #606B7C;
}

.contentright .F-1 .BlogContent-a .BlogContent-1 {
	/* width: 100%; */
	height: 25%;
	line-height: 1.5rem;
	font-size: 16px;
	font-weight: 600;
	color: black;
}

.contentright .F-1 .BlogContent-a .BlogContent-2 {
	height: 55%;
	line-height: 1.2rem;
	font-size: 14px;
	font-weight: 500;
	color: black;
}

.contentright .F-1 .BlogContent-a .BlogContent-3 {
	height: 30%;
	line-height: 20px;
	font-size: 14px;
	font-weight: 400;
	color: black;
	bottom: 0;
}

.contentright .F-1 .BlogContent-a .BlogContent-3 span {
	margin: 0 10px;
}
</style>
