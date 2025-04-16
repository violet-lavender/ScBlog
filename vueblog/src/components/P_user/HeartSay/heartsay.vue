<template>
  <div class="contain">
    <div class="F-3">
      <!-- 表白墙的部分 -->
      <div class="F-3-content">
        <!-- 预留动画部分 -->
        <div class="publishlove">
          <a @click="Publish">我要发布</a>
        </div>
        <!-- 当没有数据时显示 -->
        <div class="nodata" v-if="DynamicList.length == 0">
          <p>当前没有动态哦</p>
        </div>
        <!-- 表白墙正文显示区域 -->
        <div class="heartsay" v-for="item in DynamicList" :key="item.id">
          <div class="heartA">
            <div class="img">
							<img :src="item.user.avatarUrl" alt="">
						</div>
						<div class="username">
							{{ item.user.username }}
						</div>
					</div>
					<div class="heartB" @click="handleClick(item.id)">
						<div>
							{{ item.content }}
						</div>
					</div>
					<div class="meta-info">
						<span>{{ item.createTime }}</span>
						<span>{{ item.viewNum || 0 }} 浏览</span>
						<span>{{ item.likesNum || 0 }} 点赞</span>
					</div>
					<div class="heartC">
						<ul>
							<li>
								<div class="like-wrapper">
									<svg class="icon like-icon" :class="{ active: item.isLike || false }" @click="Like(item.id)"
											 :disabled="loadingStates[item.id]" aria-hidden="true">
										<use xlink:href="#icon-dianzan_kuai"></use>
									</svg>
									<span class="like-count">
                    {{ item.likesNum || 0 }}
                  </span>
                </div>
              </li>
							<li v-for="(icon, i) in iconList" :key="i">
								<div class="icon-wrapper" @click="handleFeatureClick(i)">
									<svg aria-hidden="true" class="icon">
										<use :xlink:href="icon"></use>
									</svg>
								</div>
							</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <infinite-loading spinner="spiral" @infinite="infiniteHandler" :distance="200" class="infinite-loading-wrap">
      <div slot="spinner">加载中...</div>
      <div slot="no-more">暂无更多数据</div>
      <div slot="no-results">No results Data</div>
      <div slot="error" slot-scope="{ trigger }">
        Error Data, click
        <a href="javascript:;" @click="trigger">here</a> to retry
      </div>
    </infinite-loading>
  </div>
</template>

<script>
import qs from 'qs'

import InfiniteLoading from 'vue-infinite-loading'

export default {
  data() {
    return {
      avatarUrl: '',
			username: '',
			iconList: ['#icon-pinglun', '#icon-zhuanfa', '#icon-gengduo'],
			loadingStates: {},  // 加载状态跟踪
			components: {InfiniteLoading},
			page: 1,
			hasMore: true,  // 是否还有更多数据
			// 动态列表
			DynamicList: []
		}
	},
	created() {
	},
	components: {InfiniteLoading},
	methods: {
		async infiniteHandler($state) {
			if (!this.hasMore) {
				$state.complete()
				return
			}

			try {
				const config = {
          params: {
            page: this.page
          },
          headers: { token: localStorage.getItem("token") }
        }

        const res = await this.$axios.get('/blink/list', config)

        if (res.data?.status) {
          const records = res.data.data.records
          this.DynamicList = this.DynamicList.concat(
            records.map(item => ({
              ...item,
              isLike: item.isLike ?? false
            }))
          )

          this.page++
          this.hasMore = this.page <= res.data.data.pages
          $state.loaded()

					if (!this.hasMore) {
						$state.complete()
					}
				}
			} catch (error) {
				$state.error()
				console.error("加载失败:", error)
			}
		},
		// 点击动态, 增加浏览量
		async handleClick(blinkId) {
			if (this.loadingStates[blinkId]) return;
			this.$set(this.loadingStates, blinkId, true);

			try {
				await this.$axios.post('/blink/view',
					qs.stringify({id: blinkId}),
					{headers: {token: localStorage.getItem("token")}}
				);
			} catch (error) {
				console.error('浏览量上报失败:', error);
			} finally {
				this.$delete(this.loadingStates, blinkId);
			}
		},
		// 跳转至编辑动态页面
		Publish() {
			var routeUrl = this.$router.resolve({path: './DynamicEdit'})
			window.open(routeUrl.href, '_blank');
		},
		// 点赞函数
		async Like(blinkId) {
			if (!blinkId || this.loadingStates[blinkId]) return;

			const index = this.DynamicList.findIndex(item => item.id === blinkId);
      if (index === -1) return;

      const target = this.DynamicList[index];
      if (!target) return;

      // 初始化字段
      if (typeof target.isLike === 'undefined') {
        this.$set(target, 'isLike', false);
      }
      if (typeof target.likesNum === 'undefined') {
        this.$set(target, 'likesNum', 0);
      }

      const originalLike = target.isLike;
      const originalCount = target.likesNum;

      try {
        this.$set(this.loadingStates, blinkId, true);

        // 乐观更新
        target.isLike = !originalLike;
        target.likesNum += originalLike ? -1 : 1;

        const res = await this.$axios.post('/blink/like',
          qs.stringify({ blinkId }),
          { headers: { token: localStorage.getItem("token") } }
        );

        if (res.data.code !== 200) {
          throw new Error(res.data.message || '操作失败');
        }

        // 同步后端准确数据
        target.likesNum = res.data.data.num;
        target.isLike = res.data.data.status;

      } catch (error) {
        // 回滚
        target.isLike = originalLike;
        target.likesNum = originalCount;
        this.$message.error(error.message);
      } finally {
        this.$delete(this.loadingStates, blinkId);
      }
    },
    handleFeatureClick(index) {
      const features = ['评论', '分享', '其他'];
      this.$message({
        message: `当前暂未开发${features[index]}功能哦，敬请期待更多功能~`,
        type: 'info',
        duration: 3000,
        customClass: 'feature-message'
      });
    }
  },

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

.contain {
	background: var(--bg-gradient);
	min-height: 100vh;
	padding: 20px 0;
	font-family: 'Helvetica Neue', sans-serif;
	color: var(--text-dark);
}

.F-3 {
	width: 100%;
	max-width: 1120px;
	margin: 0 auto;
	border-radius: 16px;
	background: #ffe8ef;
	box-shadow: 0 8px 24px rgba(255, 122, 163, 0.12);
	border: 1px solid rgba(255, 122, 163, 0.18);
	overflow: hidden;
	transition: all 0.3s ease;
}

.F-3-content {
	padding: 32px 0;
}

.publishlove {
	padding: 0 6% 30px;
	text-align: right;
}

.publishlove a {
	display: inline-block;
	padding: 12px 36px;
	background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
	color: #fff5f7 !important;
	/* 改为柔和白色 */
	border-radius: 32px;
	font-size: 18px;
	font-weight: 600;
	transition: all 0.3s ease;
	box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
	text-decoration: none;
}

.publishlove a:hover {
	transform: translateY(-3px);
	box-shadow: 0 10px 24px rgba(255, 107, 157, 0.4);
	background: linear-gradient(45deg, var(--secondary-color), var(--primary-color));
}

.nodata {
	padding: 60px 0;
	font-size: 18px;
	color: var(--text-dark);
	opacity: 0.8;
	text-align: center;
}

.heartsay {
	margin: 24px auto;
	width: 90%;
	background: var(--white);
	border-radius: 16px;
	padding: 24px;
	box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
	transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.heartsay:hover {
	transform: translateY(-3px);
	box-shadow: 0 8px 20px rgba(255, 122, 163, 0.15);
}

.heartA {
	display: flex;
	align-items: center;
	margin-bottom: 15px;
}

.heartA .img {
	width: 56px;
	height: 56px;
	border-radius: 50%;
	overflow: hidden;
	background: #fff;
	border: 2px solid var(--primary-color);
	box-shadow: 0 2px 6px rgba(255, 107, 157, 0.15);
	flex-shrink: 0;
}

.heartA .img img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.heartA .username {
	font-size: 18px;
	font-weight: 600;
	color: var(--text-dark);
	margin-left: 16px;
	letter-spacing: 0.5px;
}

.heartB {
	padding: 18px;
	margin: 15px 0;
	background: #fff0f5;
	border-radius: 12px;
	font-size: 16px;
	line-height: 1.7;
	color: var(--text-light);
	border: 2px solid transparent;
	transition: all 0.3s ease;
	white-space: pre-wrap;
	word-break: break-word;
	/* 新增修复样式 */
	text-indent: 0;
	text-align: justify;
	text-justify: inter-ideograph;
}

.heartB {
	/* 保持原有基础样式 */
	padding: 18px;
	margin: 15px 0;
	background: #fff0f5;
	border-radius: 12px;
	font-size: 16px;
	line-height: 1.7;
	color: var(--text-light);
	transition: all 0.3s ease;
	white-space: pre-wrap;
	word-break: break-word;

	/* 新增修正属性 */
	text-align: left !important;
	/* 强制左对齐 */
	text-indent: 0 !important;
	/* 清除首行缩进 */
	padding-left: 0;
	/* 清除左侧内间距 */
}

.heartB:hover {
	border-color: rgba(255, 107, 157, 0.2);
	background: rgba(255, 235, 238, 0.35);
}

.heartC ul {
	display: flex;
	justify-content: flex-end;
	gap: 16px;
	padding: 8px 15px 0;
}

.heartC li {
	display: flex;
	align-items: center;
}

.like-wrapper,
.icon-wrapper {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 6px;
	padding: 6px 12px;
	min-height: 36px;
	min-width: 48px;
	border-radius: 20px;
	background: rgba(255, 107, 157, 0.08);
	transition: background 0.3s ease;
	cursor: pointer;
}

.like-wrapper:hover,
.icon-wrapper:hover {
	background: rgba(255, 107, 157, 0.15);
}

.heartC svg {
	width: 24px;
	height: 24px;
	fill: #999;
	transition: all 0.2s ease;
}

.like-icon.active {
	fill: var(--active-color) !important;
	filter: drop-shadow(0 2px 4px rgba(255, 107, 157, 0.5));
}

.like-count {
	font-size: 14px;
	font-weight: 500;
	color: var(--text-dark);
}

.heartC li:not(:first-child) svg {
	background: none;
	padding: 0;
	border-radius: 0;
}

.heartC li:not(:first-child) svg:hover {
	background: rgba(255, 107, 157, 0.15);
	transform: scale(1.15);
	fill: var(--primary-color);
}

.infinite-loading-wrap {
	padding: 30px 0;
	color: var(--text-dark);
}

.infinite-loading-wrap div[slot="spinner"] {
	font-size: 14px;
	color: var(--primary-color);
}

.infinite-loading-wrap div[slot="no-more"] {
	color: #aaa;
}

.meta-info {
	font-size: 13px;
	color: #999;
	margin-top: -10px;
	margin-bottom: 8px;
	padding-left: 4px;
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
}

.meta-info span {
	white-space: nowrap;
}
</style>
