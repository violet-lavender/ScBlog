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
                        <div ref="writeBlog" class="writeBlog">
                            <div>您还没有发布任何博客哦，去创作中心试试吧！</div>
                            <button @click="TurnToWriteBlog">写博客</button>
                        </div>
                        <div ref="noneSearch" class="nonesearch">
                            没有搜索到相关内容！
                        </div>
                        <div ref="firstContent" class="show-content">
                            <div class="F-1" v-for="(item, index) in List" :key="index">
                                <div class="BlogContent-a">
                                    <div @click="TurnToShow(item.id)" class="BlogContent-1">{{ item.title }}</div>
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
                                    <el-popover placement="top" width="160" v-model="visible2">
                                        <p>确定删除吗？</p>
                                        <div style="text-align: right; margin: 0">
                                            <el-button size="mini" type="text" @click="visible2 = false">取消</el-button>
                                            <el-button type="primary" size="mini"
                                                @click="visible2 = false">确定</el-button>
                                        </div>
                                        <el-button style="pointer-events: auto;" slot="reference">删除</el-button>
                                    </el-popover>
                                </div>
                            </div>
                            <!--infinite-loading这个组件要放在列表的底部，滚动的盒子里面-->
                            <infinite-loading spinner="spiral" @infinite="infiniteHandler" :distance="200"
                                class="infinite-loading-wrap">
                                <div slot="spinner">Loading...</div>
                                <div slot="no-more">No more Data</div>
                                <div slot="no-results">No results Data</div>
                                <div slot="error" slot-scope="{ trigger }">
                                    Error Data, click <a href="javascript:;" @click="trigger">here</a> toretry
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
            ScreenList: [{ title: "状态" }, { title: "全部", chose: true }, { title: "全部可见", chose: false }, { title: "仅我可见", chose: false }, { title: "审核", chose: false }, { title: "草稿箱", chose: false }, { title: "回收站", chose: false }],
            // 显示全部的列表
            allList: [],
            // 头部导航选择的部分
            activeName: 'first',
            //  时间选择
            timechose: "",
            // 显示遍历的列表
            List: [],
            // 博客页数
            page: 0,
            // 搜索博客
            searchblog: "",
            config: {
                params: { status: 0, page: 0, pageSize: 20, month: null },
                headers: {
                    'token': localStorage.getItem('token')
                }
            },
            visible2: false,
            isSearching: false,
            searchParams: {}
        }
    },
    async mounted() {
        // 获取全部用户博客数据显示各种状态下的数据
        //    await this.GetData()
        this.$refs.noneSearch.style.display = "none";
    },
    methods: {
        // 添加日期格式化方法
        formatMonth(date) {
            if (!date) return null;
            const year = date.getFullYear();
            const month = date.getMonth() + 1;
            return `${year}-${month.toString().padStart(2, '0')}`;
        },
        async GetData() {
            this.List = [];
            this.config.params.month = this.formatMonth(this.timechose);

            try {
                const res = await this.$axios.get("/blog/console/list", this.config);
                this.List = res.data.data.records; // 直接赋值新数据
                // 更新空状态
                this.$refs.writeBlog.style.display = this.List.length ? "none" : "block";
            } catch (error) {
                console.error("数据加载失败:", error);
                this.$message.error("数据加载失败");
            }
        },
        // 手动选择部分
        handleClick(tab, event) {
            console.log(tab, event);
        },
        // 修改后的搜索方法
        async SearchBlog() {
            this.isSearching = true
            this.searchParams = {
                key: this.searchblog,
                status: this.config.params.status,
                month: this.formatMonth(this.timechose),
                page: 1,  // 搜索从第一页开始
                pageSize: this.config.params.pageSize
            };

            try {
                const res = await this.$axios.get("/blog/console/search", {
                    params: this.searchParams,
                    headers: this.config.headers
                })

                if (res.data.data.records.length === 0) {
                    this.$refs.firstContent.style.display = "none"
                    this.$refs.noneSearch.style.display = "block"
                    this.List = []
                } else {
                    this.List = res.data.data.records
                    this.$refs.firstContent.style.display = "block"
                    this.$refs.noneSearch.style.display = "none"
                }
            } catch (error) {
                console.error("搜索失败:", error)
                this.$message.error("搜索失败，请稍后重试")
            }
        },
        //   滑动触底时调用, 无限滚动处理
        async infiniteHandler($state) {
            try {
                if (this.isSearching) {
                    // 处理搜索分页
                    this.searchParams.page++
                    const res = await this.$axios.get("/blog/console/search", {
                        params: this.searchParams,
                        headers: this.config.headers
                    })

                    this.handlePaginationData(res, $state)
                } else {
                    // 处理普通列表分页
                    this.config.params.page++
                    const res = await this.$axios.get("/blog/console/list", this.config)

                    this.handlePaginationData(res, $state)
                }
            } catch (error) {
                $state.error()
                console.error("加载失败:", error)
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
        TurnToShow(index) {
            var routeUrl = this.$router.resolve({ name: 'BlogDetail', params: { blogId: index } })
            window.open(routeUrl.href, '_blank');
        },
        // 点击后跳转至编写博客页面
        TurnToWriteBlog() {
            this.$router.push('/blog/add')
        },
        // 选择限制条件
        ChoseScreen(index) {
            if (index != 0) {
                for (let i = 1; i < this.ScreenList.length; i++) {
                    this.ScreenList[i].chose = false
                }
                this.ScreenList[index].chose = true
                this.isSearching = false
                this.searchblog=""
                this.config.params.status = (index - 1).toString()
                this.config.params.page = 1
                this.GetData()//所有博客
            }
        }
    },
    watch: {
        timechose: {
            handler: _.debounce(function (newVal) {
                this.config.params.page = 1;
                this.isSearching = false;
                this.GetData();
            }, 300),
            immediate: true
        }
    }
}
</script>

<style scoped>
.managecontent {
    width: calc(100%- 40px);
    height: calc(100%- 40px);
    margin: 20px;
}

/* 顶部筛选条件 */
.screening-conditions {
    width: 100%;
    height: 50px;
}

.screening-conditions ul {
    width: 100%;
    height: 100%;
    list-style: none;
}

.screening-conditions ul li {
    width: 13%;
    height: 100%;
    float: left;
    font-size: 16px;
    color: rgb(136, 137, 138);
}

.time-chose {
    width: 100%;
    height: 60px;
    border-bottom: 2px solid rgb(217, 217, 217);
}

.time-chose .chosemonth {
    width: 30%;
    height: 100%;
    float: left;
}

.time-chose .search {
    width: 40%;
    height: 100%;
    float: right;

}

.screening-conditionsB {
    width: 100%;
    /* height: 500px; */

}

.screening-conditionsB .writeBlog {
    width: 100%;
    height: 500px;
    text-align: center;
    /* display: flex;
    align-items: center;
    justify-content: center; */
}

.screening-conditionsB .nonesearch {
    width: 100%;
    height: 500px;
    font-size: 20px;
    font-weight: 800;
    color: red;
    text-align: center;
    /* display: flex;
    align-items: center;
    justify-content: center; */
}

.screening-conditionsB .writeBlog div {
    width: 100%;
    height: 20%;
    font-size: 20px;
    font-weight: 800;
    position: relative;
    top: 30%;
    transform: translateY(-50%);
}

.screening-conditionsB .writeBlog button {
    width: 150px;
    height: 50px;
    border: none;
    background: rgb(242, 88, 28);
    color: white;
    border-radius: 20px;
    font-size: 20px;
    font-weight: 600;
    position: relative;
    top: 20%;
    transform: translateY(-50%);
}

.screening-conditionsB .show-content {
    width: 100%;
    height: 100%;
}

.screening-conditionsB .show-content .F-1 {
    width: 100%;
    height: 30%;
}

.BlogContent-a {
    width: 80%;
    height: 100%;
    display: inline-block;
    padding: 10px 0;
    border-bottom: 1px solid #b7b8bb;
}

.BlogContent-b {
    width: 20%;
    height: 100%;
    display: inline-block;
    /* border-bottom: 1px solid #b7b8bb; */
}

.BlogContent-1 {
    width: 100%;
    height: 30%;
    font-size: 22px;
    font-weight: 600;
    color: black;
}

.BlogContent-1:hover {
    color: rgb(252, 85, 49);
}

.BlogContent-2 {
    width: 100%;
    height: 20%;
    margin: 10px 0;
    font-size: 16px;
    font-weight: 600;
    color: #555666;
}

.BlogContent-3 {
    width: 100%;
    height: 20%;
    font-size: 16px;
    font-weight: 600;
    color: #555666;
}

.BlogContent-3 span {
    margin: 0 5px;
}
</style>