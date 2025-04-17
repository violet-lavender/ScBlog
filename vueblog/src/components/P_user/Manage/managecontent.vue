<template>
    <div class="managecontent">
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="文章" name="first">
                <!-- <div class="searchResult">
            </div> -->
                <div class="firstmodel">
                    <div class="screening-conditions">
                        <ul>
                            <li v-for="(item, index) in ScreenList" :key="index" @click="ChoseScreen(index)"
                                :style="{ 'color': item.chose ? 'black' : '' }">{{ item.title }}</li>
                        </ul>
                    </div>
                    <div class="screening-conditionsB">
                        <div class="time-chose">
                            <!-- 月份选择 -->
                            <div class="chosemonth">
                                <el-date-picker v-model="timechose" type="month" placeholder="选择月">
                                </el-date-picker>
                            </div>
                            <!-- 对博客列表进行搜索 -->
                            <div class="search">
                                <el-input placeholder="请输入内容" v-model="searchblog" class="input-with-select">
                                    <el-button @click="SearchBlog" slot="append" icon="el-icon-search"></el-button>
                                </el-input>
                            </div>
                        </div>
                        <div v-if="showNoBlog" class="writeBlog">
													<div>您还没有发布任何博客哦，去创作中心试试吧！</div>
													<button @click="TurnToWriteBlog">写博客</button>
												</div>
											<div v-if="showNoSearchResult" class="nonesearch">
												没有搜索到相关内容！
											</div>
                        <div ref="firstContent" class="show-content">
                            <div class="F-1" v-for="(item, index) in List" :key="index">
                                <div class="BlogContent-a">
																	<div class="BlogContent-1" @click="TurnToShow(item)">{{ item.title }}</div>
                                    <div class="BlogContent-2">{{ item.description }}</div>
                                    <div class="BlogContent-3">
                                        {{ item.releaseTime }}.
                                        <span>{{ item.viewNum }}阅读 .</span>
                                        <span>{{ item.likeNum }}点赞 .</span>
                                        <span>{{ item.commentNum }}评论 .</span>
                                        <span>{{ item.collectionNum }}收藏</span>
                                    </div>
                                </div>
                                <div class="BlogContent-b">
                                    <!-- 修改删除弹窗部分 -->
                                    <el-popover placement="top" width="160" v-model="popoverVisible[item.id]">
                                        <p>
																					{{
																						item.status === 2 ? '确定删除草稿吗？草稿删除不可恢复'
																							: item.status === 4 ? '确定永久删除该博客吗？该操作不可逆'
																								: '确定删除该博客吗？您可以在回收站中恢复'
																					}}
																				</p>
                                        <div style="text-align: right; margin: 0">
                                            <el-button size="mini" type="text"
                                                @click="popoverVisible[item.id] = false">取消</el-button>
                                            <el-button type="primary" size="mini"
                                                @click="handleDeleteConfirm(item.id, item.status)">
                                                确定
                                            </el-button>
                                        </div>
                                        <el-button slot="reference"
                                            :type="item.status === 2 || item.status === 4 ? 'danger' : 'text'"
                                            style="pointer-events: auto;">
																					删除
																				</el-button>
																		</el-popover>
																	<el-button v-if="item.status === 4" type="success"
																						 style="margin-left:10px;padding:12px 15px"
																						 @click="handleRecovery(item.id)">
																		<i class="el-icon-refresh-left"></i> 恢复
																	</el-button>
																</div>
														</div>
													<!--infinite-loading这个组件要放在列表的底部，滚动的盒子里面-->
													<infinite-loading ref="infiniteLoading" :distance="200" class="infinite-loading-wrap"
																						spinner="spiral" @infinite="infiniteHandler">
														<div slot="spinner">Loading...</div>
														<div slot="no-more">No more Data</div>
														<div slot="no-results">No results Data</div>
														<div slot="error" slot-scope="{ trigger }">
															Error Data, click <a href="javascript:;" @click="trigger">here</a> to retry
														</div>
													</infinite-loading>
												</div>
                    </div>
                </div>
            </el-tab-pane>
            <el-tab-pane label="下载" name="second">

            </el-tab-pane>
            <el-tab-pane label="问答" name="third">

            </el-tab-pane>
            <el-tab-pane label="视频" name="fourth">

            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
import InfiniteLoading from 'vue-infinite-loading'
import _ from 'lodash'

export default {
    name: "ManageContent",
    components: {
        InfiniteLoading
    },
    data() {
        return {
            // status选择部分列表
            ScreenList: [
                { title: "状态" },
                { title: "全部", chose: true, status: 0 },      // 0表示所有状态
                { title: "已发表", chose: false, status: 1 },    // 对应status=1
                { title: "仅我可见", chose: false, status: 3 },  // 对应status=3
                { title: "审核中", chose: false, status: 5 },    // 对应status=5
                { title: "草稿箱", chose: false, status: 2 },    // 对应status=2
                { title: "回收站", chose: false, status: 4 }     // 对应status=4
            ],
					// 显示全部的列表
					allList: [],
					// 头部导航选择的部分
					activeName: 'first',
					//  时间选择
					timechose: "",
					// 显示遍历的列表
					List: [],
					// 搜索博客
					searchblog: "",
					listParams: {  // 普通列表参数
						status: 0,
						page: 1,    // 从1开始
						pageSize: 20,
						month: null
					},
					searchParams: {  // 搜索参数
						key: '',
						status: 0,
						page: 1,
						pageSize: 20,
						month: null
					},
					headers: {
						'token': localStorage.getItem('token')
					},
					visible2: false,
					isSearching: false,
					isLoading: false,   // 加载锁
					popoverVisible: {},
					showNoBlog: false,
					showNoSearchResult: false,
				}
		},
	async created() {
	},
	mounted() {
	},
	watch: {
		timechose: {
			handler: _.debounce(function (newVal) {
				// 重置分页参数
				this.listParams = {
					...this.listParams,
					month: this.formatMonth(newVal),
					page: 1
				};
				this.isSearching = false;
				this.GetData();
			}, 300),
			immediate: true
		}
	},
	methods: {
		// 状态映射方法
		getStatusText(status) {
			const statusMap = {
				0: '全部',
				1: '已发表',
				2: '草稿箱',
				3: '仅我可见',
				4: '回收站',
                5: '审核中'
            }
            return statusMap[status] || '未知状态'
        },
        // 添加日期格式化方法
        formatMonth(date) {
            if (!date) return null;
            const year = date.getFullYear();
            const month = date.getMonth() + 1;
            return `${year}-${month.toString().padStart(2, '0')}`;
        },
        async GetData() {
					this.List = [];
					this.listParams.month = this.formatMonth(this.timechose);

            try {
							const res = await this.$axios.get("/blog/console/list", {
								params: this.listParams,
								headers: this.headers
							});

							this.List = res.data.data.records;
							// 重置无限加载组件
							this.$nextTick(() => {
								if (this.$refs.infiniteLoading) {
									this.$refs.infiniteLoading.stateChanger.reset();
								}
							});
							this.updateEmptyState();
						} catch (error) {
                console.error("数据加载失败:", error);
            }
        },
        // 手动选择部分
        handleClick(tab, event) {
            console.log(tab, event);
        },
        // 修改后的搜索方法
        async SearchBlog() {
					this.isSearching = true;
					this.searchParams = {
						key: this.searchblog,
						status: this.listParams.status,
						month: this.formatMonth(this.timechose),
						page: 1,
						pageSize: 20
					};

            try {
							const res = await this.$axios.get("/blog/console/search", {
								params: this.searchParams,
								headers: this.headers
							});

							this.List = res.data.data.records;
							this.updateEmptyState();
							// 重置无限加载组件
							this.$nextTick(() => {
								if (this.$refs.infiniteLoading) {
									this.$refs.infiniteLoading.stateChanger.reset();
								}
							});
						} catch (error) {
							console.error("搜索失败:", error);
            }
        },
        //   滑动触底时调用, 无限滚动处理
        async infiniteHandler($state) {
					// 防止并发请求
					if (this.isLoading) return;
					this.isLoading = true;

					try {
						let params, apiUrl;
						if (this.isSearching) {
							params = {...this.searchParams};
							apiUrl = "/blog/console/search";
						} else {
							params = {...this.listParams};
							apiUrl = "/blog/console/list";
						}

						const res = await this.$axios.get(apiUrl, {
							params: params,
							headers: this.headers
						});

						const records = res.data.data?.records || [];
						if (records.length) {
							this.List = this.List.concat(records); // 确保数据追加

							// 更新对应页码
							if (this.isSearching) {
								this.searchParams.page++;
							} else {
								this.listParams.page++;
							}

							$state.loaded();
						} else {
							$state.complete();
						}
					} catch (error) {
						$state.error();
					} finally {
						this.isLoading = false;
					}
        },
        // 公共分页数据处理
        handlePaginationData(res, $state) {
            const newData = res.data.data?.records || []
            const pagination = res.data.data

            if (newData.length) {
                this.List = this.List.concat(newData)
                $state.loaded()

                // 检查是否还有更多数据
                if (pagination.current >= pagination.pages) {
                    $state.complete()
                }
            } else {
							$state.complete()
						}

					// 更新空状态显示
					if (this.List.length > 0) {
						this.$refs.writeBlog.style.display = "none"
						this.$refs.noneSearch.style.display = "none"
					}
				},
		// 跳转至博客具体内容列表
		TurnToShow(item) {
			// 草稿状态跳转编辑页
			if (item.status === 2) {
				const routeUrl = this.$router.resolve({
					name: 'BlogEdit',
					params: {blogId: item.id}
				});
				window.open(routeUrl.href, '_blank');
			}
			// 其他状态跳转详情页
			else if (item.status !== 4) {
				const routeUrl = this.$router.resolve({
					name: 'BlogDetail',
					params: {blogId: item.id}
				});
				window.open(routeUrl.href, '_blank');
			}
		},
		// 点击后跳转至编写博客页面
		TurnToWriteBlog() {
			this.$router.push('/blog/add')
		},
		// 修改状态筛选方法
		ChoseScreen(index) {
			if (index != 0) {
				this.ScreenList.forEach((item, i) => {
					item.chose = i === index;
				});

				// 重置分页参数
				this.listParams = {
					...this.listParams,
					status: this.ScreenList[index].status,
					page: 1
				};

				this.isSearching = false;
				this.searchblog = "";
				this.GetData();
			}
        },
        // 删除处理方法
        async handleDeleteConfirm(blogId, status) {
            try {
                this.popoverVisible[blogId] = false;

                // 判断是否应该直接删除（状态2草稿或状态4回收站）
                const isCompletelyDelete = [2, 4].includes(status);
                const apiUrl = isCompletelyDelete ? '/blog/console/blog/delete' : 'blog/console/blog';

                const res = await this.$axios.delete(apiUrl, {
                    params: { id: blogId },
                    headers: this.config.headers
                });

                if (res.data.code === 200) {
                    // 更新列表数据
                    if (isCompletelyDelete) {
                        this.List = this.List.filter(item => item.id !== blogId);
                    } else {
                        // 修改为正确的状态更新逻辑
                        this.List = this.List.map(item => {
                            if (item.id === blogId) {
                                return { ...item, status: 4 }; // 更新状态为回收站
                            }
                            return item;
                        });
                    }

                    this.$message.success(isCompletelyDelete ? '删除成功' : '已移入回收站');
                    this.$forceUpdate();
                }
            } catch (error) {
                console.error('删除失败:', error);
                this.$message.error('操作失败: ' + (error.response?.data?.message || '未知错误'));
            }
        },
        // 恢复处理方法
        async handleRecovery(blogId) {
            try {
                const res = await this.$axios.put('/blog/console/blog/recovery', null, {
                    params: { id: blogId },
                    headers: this.config.headers
                });

                if (res.data.code === 200) {
                    // 如果是回收站列表直接过滤
                    if (this.config.params.status === 4) {
                        this.List = this.List.filter(item => item.id !== blogId);
                    }
										// 其他情况需要刷新数据
										else {
											await this.GetData();
										}
									this.$message.success('恢复成功');
								}
						} catch (error) {
							console.error('恢复失败:', error);
							this.$message.error('恢复失败: ' + (error.response?.data?.message || '未知错误'));
						}
				},
		updateEmptyState() {
			const hasData = this.List.length > 0;

			if (this.isSearching) {
				this.showNoSearchResult = !hasData;
				this.showNoBlog = false;
			} else {
				this.showNoBlog = !hasData;
				this.showNoSearchResult = false;
			}
		}
	},
}
</script>

<style scoped>
.managecontent {
	padding: 20px;
	background: linear-gradient(135deg, #f8f9fb, #eef1f6);
	min-height: 100%;
}

.el-tabs {
	background: #ffffff;
	border-radius: 12px;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
	padding: 10px 20px;
}

.screening-conditions {
	padding: 10px 0;
}

.screening-conditions ul {
	display: flex;
	gap: 10px;
	flex-wrap: wrap;
	padding-left: 0;
	margin: 0;
}

.screening-conditions ul li {
	list-style: none;
	padding: 6px 16px;
	font-size: 14px;
	color: #888;
	background-color: #f4f4f5;
	border-radius: 20px;
	cursor: pointer;
	transition: all 0.3s ease;
}

.screening-conditions ul li:hover,
.screening-conditions ul li[style*='color: black'] {
	background-color: #409EFF;
	color: #fff !important;
	font-weight: 600;
}

.time-chose {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 0 20px;
	border-bottom: 1px solid #e5e5e5;
}

.chosemonth,
.search {
	flex: 1;
	margin-right: 10px;
}

.writeBlog,
.nonesearch {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	height: 300px;
	background: #fff;
	border-radius: 12px;
	margin-top: 20px;
	box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
}

.writeBlog div {
	font-size: 18px;
	font-weight: 600;
	color: #666;
	margin-bottom: 20px;
}

.writeBlog button {
	background-color: #409EFF;
	color: #fff;
	padding: 10px 24px;
	border-radius: 24px;
	border: none;
	font-size: 16px;
	transition: 0.3s ease;
	cursor: pointer;
}

.writeBlog button:hover {
	background-color: #66b1ff;
}

.nonesearch {
	font-size: 18px;
	font-weight: 600;
	color: #f56c6c;
}

.show-content {
	margin-top: 20px;
}

.F-1 {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	background: #ffffff;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
	transition: all 0.3s ease;
}

.F-1:hover {
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.BlogContent-a {
	flex: 1;
	padding-right: 20px;
}

.BlogContent-1 {
	font-size: 18px;
	font-weight: 700;
	color: #303133;
	margin-bottom: 8px;
	cursor: pointer;
	transition: 0.3s ease;
}

.BlogContent-1:hover {
	color: #409EFF;
}

.BlogContent-2 {
	font-size: 14px;
	color: #606266;
	margin-bottom: 10px;
}

.BlogContent-3 {
	font-size: 12px;
	color: #909399;
}

.BlogContent-3 span {
	margin-left: 10px;
}

.BlogContent-b {
	display: flex;
	align-items: center;
}

.BlogContent-b .el-button {
	margin-left: 10px;
	border-radius: 6px;
	padding: 10px 16px !important;
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
	font-size: 14px;
	font-weight: 500;
	transition: all 0.3s ease;
}

/* 删除按钮 - 蓝色（移入回收站） */
.BlogContent-b .el-button--text {
	color: #409EFF;
	background-color: #ecf5ff;
	border: 1px solid #d9ecff;
}

/* 删除按钮 - 红色（彻底删除） */
.BlogContent-b .el-button--danger {
	background-color: #fde2e2;
	border: 1px solid #fbc4c4;
	color: #f56c6c;
}

/* 恢复按钮 - 绿色 */
.BlogContent-b .el-button--success {
	background-color: #e1f3d8;
	border: 1px solid #c2e7b0;
	color: #67c23a;
}

.infinite-loading-wrap {
	margin-top: 20px;
	text-align: center;
	color: #999;
	font-size: 14px;
}

/* 居中提示内容 */
.writeBlog {
	margin-top: 80px;
	text-align: center;
	font-size: 16px;
	color: #999;
}

.writeBlog button {
	margin-top: 16px;
	padding: 10px 20px;
	background-color: #409EFF;
	color: #fff;
	border: none;
	border-radius: 8px;
	cursor: pointer;
	box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
	transition: background-color 0.3s ease;
}

.writeBlog button:hover {
	background-color: #66b1ff;
}

/* 美化确认删除弹窗 */
.el-popover {
	max-width: 400px !important;
	border-radius: 12px;
	padding: 16px !important;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
	font-size: 14px;
	line-height: 1.6;
}

.el-popover .el-button {
	margin-left: 8px;
}

.el-popover .el-button--mini {
	border-radius: 8px;
	padding: 6px 12px;
}

.nonesearch {
	text-align: center;
	color: #999;
	font-size: 16px;
	margin-top: 40px;
}
</style>
