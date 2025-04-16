<template>
    <div>
        <div class="F-2">
            <div class="F-2-content">
                <div class="F-2-A">
									<div class="A-1">
										{{ isOwner ? '我的动态' : 'TA的动态' }}
									</div>
									<div v-if="isOwner" class="A-2" @click="TurnToDynamicEdit">
										+发布动态
									</div>
									<div v-if="isOwner" class="A-3">
										<span>允许他人查看</span>
										<div class="switch">
											<el-form ref="form" :model="form">
												<el-switch v-model="allowview"></el-switch>
											</el-form>
										</div>
									</div>
								</div>
							<div v-for="item in DynamicList" :key="item.id" class="F-2-B">
								<div class="textcontent">
									<div class="text" @click="handleClick(item.id)">
										{{ item.content }}
									</div>
								</div>
								<!-- 元数据展示区域 -->
								<div class="meta-info">
									<span class="time">{{ item.createTime }}</span>
									<span class="views">{{ item.viewNum || 0 }}浏览</span>
									<span class="likes">{{ item.likesNum || 0 }}点赞</span>
								</div>
								<!-- 删除按钮 添加 .stop 防止冒泡 -->
								<div v-if="isOwner" class="delete-wrapper">
									<img alt="删除" class="delete-icon"
											 src='../../../../assets/icons/delete.svg' @click.stop="handleDelete(item.id)">
								</div>
							</div>
							<div v-if="nodynamictip" class="nodynamic">
								{{ isOwner ? '您当前还没有发布过动态哦！' : '该用户暂时没有发布过动态哦' }}
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

export default {
    name: 'Dynamic',
    props: {
        userId: {
					type: Number,
					required: true,
					default: 0
				},
    },
    data() {
			return {
				DynamicList: [],
				allowview: false,
				page: 1,
				hasMore: true,
				nodynamictip: false,
				loadingStates: {},
				localUserId: JSON.parse(localStorage.getItem('userMessage') || '{}').id || 0
			}
		},
	computed: {
		isOwner() {
			return Number(this.localUserId) === Number(this.userId)
		}
	},
	methods: {
		async infiniteHandler($state) {
			if (!this.hasMore) {
				$state.complete()
				return
			}

			try {
				const res = await this.$axios.get('/blink/list/self', {
					params: {
						userId: this.userId,
						page: this.page
					},
					headers: {
						token: localStorage.getItem('token')
					}
				})

                if (res.data?.status) {
									const records = res.data.data.records
									this.DynamicList = this.DynamicList.concat(records)
									this.page++
									this.hasMore = this.page <= res.data.data.pages

									// 空状态处理
									this.nodynamictip = records.length === 0 && this.page === 1

									$state.loaded()
									if (!this.hasMore) $state.complete()
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
				await this.$axios.post(
					'/blink/view',
					qs.stringify({id: blinkId}),
					{
						headers: {
							token: localStorage.getItem("token"),
							'Content-Type': 'application/x-www-form-urlencoded'
						}
					}
				);
			} catch (error) {
				console.error('浏览量上报失败:', error);
            } finally {
                this.$delete(this.loadingStates, blinkId);
            }
        },
		// 删除方法保持不变
        async handleDelete(blinkId) {
            try {
                await this.$confirm('确定删除这条动态吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })

                await this.$axios.delete('/blink', {
                    params: { blinkId },
									headers: {token: localStorage.getItem('token')}
								})

							this.DynamicList = this.DynamicList.filter(item => item.id !== blinkId)
							this.$message.success('删除成功')
						} catch (error) {
							if (error !== 'cancel') {
								this.$message.error(error.response?.data?.message || '删除失败')
							}
						}
				},
		TurnToDynamicEdit() {
			this.$router.push('/dynamic-edit')
		}
	}
}
</script>

<style scoped lang="less">
/* 整体容器 */
.F-2 {
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
	margin: 20px 0;
	/* 移除auto实现靠左对齐 */
	width: 100%;
	/* 撑满父容器 */
	max-width: 960px;
}

/* 内容区域优化 */
.F-2-content {
	padding: 0 32px;
	/* 增加水平内边距 */
}

/* 头部区域 */
.F-2-A {
	padding: 20px 24px;
	border-bottom: 1px solid #f0f0f0;
	display: flex;
	align-items: center;
	gap: 20px;

	.A-1 {
		font-size: 20px;
		font-weight: 600;
		color: #1a1a1a;
	}

	.A-2 {
		color: #06f;
		font-size: 14px;
		cursor: pointer;
		padding: 6px 12px;
		border-radius: 6px;
		transition: all 0.2s;

		&:hover {
			background: rgba(0, 102, 255, 0.06);
		}
	}

	.A-3 {
		margin-left: auto;
		display: flex;
		align-items: center;
		gap: 8px;
		color: #666;
		font-size: 14px;

		.switch {
			transform: scale(0.8);
		}
	}
}

/* 动态卡片 */
.F-2-B {
	padding: 20px 0;
	max-width: 860px;
	/* 与容器保持比例 */
	position: relative;
	transition: all 0.2s;
	border-bottom: 1px solid #f5f5f5;

	&:last-child {
		border-bottom: none;
	}

	&:hover {
		background: #fafafa;

		.delete-icon {
			opacity: 1;
		}
	}
}

/* 正文内容 */
.textcontent {
	.text {
		// 排版修正
		white-space: pre-line;
		text-indent: 0;
		padding-left: 0;
		margin: 0;

		// 增强可读性
		font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
		line-height: 1.7;
		letter-spacing: 0.02em;

		// 首行特殊处理
		&::first-line {
			font-feature-settings: "kern" 1;
			text-indent: 0;
		}

		// 长文本优化
		overflow-wrap: anywhere;
		hyphens: auto;
	}
}

/* 元数据 */
.meta-info {
	margin-top: 12px;
	display: flex;
	align-items: center;
	gap: 16px;
	font-size: 13px;
	color: #999;

	span {
		display: flex;
		align-items: center;

		&::before {
			content: '•';
			margin-right: 8px;
			color: #ddd;
		}
	}
}

/* 删除按钮 */
.delete-wrapper {
	position: absolute;
	top: 20px;
	right: 24px;
	opacity: 0.6;
	transition: all 0.2s;

	&:hover {
		opacity: 1;
		transform: scale(1.1);
	}

	.delete-icon {
		width: 18px;
		height: 18px;
		cursor: pointer;
	}
}

/* 空状态提示 */
.nodynamic {
	padding: 60px 20px;
	text-align: center;
	color: #999;
	font-size: 15px;
}

/* 无限加载样式 */
.infinite-loading-wrap {
	padding: 20px 0;
	color: #666;
	font-size: 14px;

	a {
		color: #06f;
		text-decoration: none;

		&:hover {
			text-decoration: underline;
		}
	}
}

/* 响应式适配 */
@media (min-width: 1200px) {
	.F-2 {
		max-width: 1080px; // 大屏适配
	}
}


@media (max-width: 768px) {
	.F-2 {
		margin: 12px;
		border-radius: 8px;
	}

	.F-2-content {
		padding: 0 20px; // 移动端减少边距
	}

	.F-2-A {
		padding: 16px;
		flex-wrap: wrap;
		gap: 12px;

		.A-1 {
			font-size: 18px;
		}

		.A-3 {
			margin-left: 0;
			width: 100%;
			justify-content: flex-end;
		}
	}

	.F-2-B {
		padding: 16px 0;
	}

	.meta-info {
		flex-wrap: wrap;
		gap: 8px 16px;
	}
}
</style>
