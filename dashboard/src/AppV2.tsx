import {
    Badge,
    Box,
    Button,
    chakra,
    ChakraProvider,
    Flex,
    Menu,
    MenuButton,
    MenuDivider,
    MenuItem,
    MenuList,
    Spacer,
    Tooltip,
    useMediaQuery
} from '@chakra-ui/react'
import "./AppV2.css"
import React, {useEffect, useState} from "react";
import {maxBy} from "lodash";
import {ThemeTypings} from "@chakra-ui/styled-system";
import moment from "moment";
import {ReactComponent as PersonIcon} from "@vscode/codicons/src/icons/person.svg";
import {ReactComponent as BookIcon} from "@vscode/codicons/src/icons/book.svg";
import {ReactComponent as EditIcon} from "@vscode/codicons/src/icons/edit.svg";
import {ReactComponent as MenuIcon} from "@vscode/codicons/src/icons/menu.svg";
import extra_data from "./extra.json"
import jcconf_data from "./jcconf.json"
import {useCookies} from "react-cookie";


async function fetchData() {
    // const dataSource = '/2022v2/ui-data.json';
    const dataSource = 'ui-data.json';
    const response = await (
        await fetch(dataSource, {cache: 'no-store'})
    ).text();
    const uiData = JSON.parse(response);
    return uiData;
}

interface ArticleEntry {
    title: string;
    url: string;
    iso8601Published: string;
}

interface TopicEntry {
    category: string;
    title: string;
    url: string;
    author: string;
    profileUrl: string;
    view: number;
    lastUpdated: number;
    articles: Array<ArticleEntry>;
    status: string;
}

interface UIData {
    categories: Array<string>;
    topics: Record<string, Array<TopicEntry>>;
}

interface Status {
    color: ThemeTypings["colorSchemes"];
    content: string;
    date: string;
}


interface FunctionSet {
    setAllTopic: (state: boolean) => void,
    allTopic: boolean
    selectedTopic: string,
    setSelectedTopic: (t: string) => void,
}

function BigLastUpdate(props: { topic: TopicEntry, updateToday: boolean, latestArticle: ArticleEntry }) {
    const {updateToday, latestArticle, topic} = props;

    if (topic.status == "COMPLETED") {
        return (<Flex alignItems="center">
            <a href={topic.profileUrl} target="_blank">
                <Badge className="tag" colorScheme="gray"> {topic.author}</Badge>
            </a>
            {latestArticle &&
                <Badge className="tag"
                       backgroundColor="gray.400" color="white"> <a href={latestArticle.url} target="_blank">
                    {latestArticle && latestArticle.title}</a>
                </Badge>

            }
        </Flex>)
    }

    return (<Flex alignItems="center">
        {updateToday &&
            <Badge className="tag" colorScheme="green">今日更新</Badge>
        }
        {latestArticle && !updateToday &&
            <Badge className="tag" colorScheme="red">尚未更新</Badge>
        }
        {latestArticle &&
            <Badge className="tag"
                   backgroundColor="gray.400" color="white"> <a href={latestArticle.url} target="_blank">
                {latestArticle && latestArticle.title}</a>
            </Badge>

        }
        <a href={topic.profileUrl} target="_blank">
            <Badge className="tag" colorScheme="gray"> {topic.author}</Badge>
        </a>
    </Flex>)
}

function SmallLastUpdate(props: {
    topic: TopicEntry,
    updateToday: boolean,
    latestArticle: ArticleEntry,
    status: Status
}) {
    const {updateToday, latestArticle, topic, status} = props;
    if (topic.status == "COMPLETED") {
        return (<Flex>
            <Badge colorScheme={status.color}>{status.content}</Badge>
            <Badge ml="3px" colorScheme="blackAlpha">{topic.view}</Badge>
        </Flex>)
    }

    return (
        <Flex>
            <Badge colorScheme={status.color}>{status.content}</Badge>
            <Badge ml="3px" colorScheme="blackAlpha">{topic.view}</Badge>


            {updateToday &&
                <Badge ml="3px" className="tag" colorScheme="green">今日更新</Badge>
            }
            {latestArticle && !updateToday &&
                <Badge ml="3px" className="tag" colorScheme="red">尚未更新</Badge>
            }

        </Flex>
    )
}

function Topic(props: { topic: TopicEntry, bigLayout: boolean }) {
    const {topic, bigLayout} = props;

    const latestArticle = maxBy(topic.articles, (o) => {
        return new Date(o.iso8601Published)
    });


    const STATUS_MAP = {
        ONGOING: {
            name: "安全",
            color: "blue"
        },
        NOT_STARTED: {
            name: "期待",
            color: "purple",
        },
        COMPLETED: {
            name: "完賽",
            color: "pink",
        },
        FAILED: {name: "　　", color: "blackAlpha"},
    };
    const status_tag = STATUS_MAP[topic.status] || {name: "未知", color: "red"};

    let status: Status = {
        content: status_tag.name,
        color: status_tag.color,
        date: latestArticle?.iso8601Published || ""
    };

    let updateToday = false;
    if (latestArticle) {
        if (moment(new Date(latestArticle.iso8601Published)).diff(moment.now(), 'days') == 0) {
            updateToday = true;
        }
    }

    if (bigLayout) {
        return (
            <Flex className="topic" p="2px">
                <Flex minWidth="50px" pl="15px">
                    {topic.view}
                </Flex>
                <Flex minWidth="87px" justifyContent="center">
                    <Tooltip label={status.date}>
                        <Badge pl={5} pr={5} colorScheme={status.color}>{status.content}</Badge>
                    </Tooltip>
                </Flex>
                <Flex minWidth="200px">
                    <a href={topic.url} target="_blank">
                        {topic.title}
                    </a>
                </Flex>
                <Spacer/>

                <BigLastUpdate topic={topic} latestArticle={latestArticle} updateToday={updateToday}/>
            </Flex>

        )
    }

    // smaller screen
    return (
        <Flex className="topic_small" p="5px" direction="column">
            <SmallLastUpdate topic={topic} latestArticle={latestArticle} updateToday={updateToday} status={status}/>

            <Flex mt={3}>
                <Flex minWidth="200px" alignItems="center">
                    <BookIcon style={{marginRight: "5px"}}/>
                    <a href={topic.url} target="_blank">
                        {topic.title}
                    </a>
                </Flex>
            </Flex>
            <Flex mt={3}>
                {latestArticle &&
                    <Flex direction="column">

                        <Flex alignItems="center" mb="10px">
                            <PersonIcon style={{marginRight: "3px"}}/>
                            <a href={topic.profileUrl} target="_blank">
                                <Badge className="tag" colorScheme="gray"
                                       style={{width: "fit-content"}}> {topic.author}</Badge>
                            </a>
                        </Flex>


                        <Flex alignItems="center">
                            <EditIcon style={{marginRight: "5px"}}/>
                            <a href={latestArticle.url} target="_blank"
                               style={{
                                   fontSize: "12px", width: "fit-content",
                                   color: "white", fontWeight: "700",
                                   padding: "5px", borderRadius: "5px",
                                   backgroundColor: "rgb(160, 172, 192)"
                               }}>
                                {latestArticle && latestArticle.title}</a>

                        </Flex>

                    </Flex>
                }
            </Flex>
        </Flex>

    )

}

function Category(props: { category: string, data: UIData, selectedTopic: string, allTopic: boolean }) {
    const [bigLayout] = useMediaQuery('(min-width: 750px)')
    const {category, data, selectedTopic, allTopic} = props;


    if (category === "JCConf" || category === "社群推廣區" || selectedTopic === category || selectedTopic === "所有主題") {
        return (
            <Flex className="category" direction="column">
                <Flex mb="15px">{category}</Flex>
                {
                    data.topics[category].map(t => {
                        if (allTopic) {
                            return <Topic key={t.url} topic={t} bigLayout={bigLayout}/>
                        }

                        if (t.articles.length > 0) {
                            return <Topic key={t.url} topic={t} bigLayout={bigLayout}/>
                        }

                        if (category === "社群推廣區") {
                            return <Topic key={t.url} topic={t} bigLayout={bigLayout}/>
                        }
                        if (category === "JCConf") {
                            return <Topic key={t.url} topic={t} bigLayout={bigLayout}/>
                        }

                    })
                }
            </Flex>
        )
    }


}

function TopicFilter(props: { functionSet: FunctionSet, categories: Array<string> }) {
    const {allTopic, setAllTopic} = props.functionSet
    const {selectedTopic, setSelectedTopic} = props.functionSet
    const {categories} = props

    const CMenuItem = chakra(MenuItem, {
        baseStyle: {
            _focus: {background: "rgb(44,82,130)"},
            fontSize: "10pt",
        },
    });

    const handler = (category: string) => {
        if ("no-show-filter" === category) {
            setAllTopic(!allTopic);
            return;
        }
        setSelectedTopic(category);
    }

    return (
        <>
            <Flex ml="16px" mr="16px">
                <Menu>
                    <MenuButton as={Button} leftIcon={<MenuIcon/>} colorScheme="blue">
                        {selectedTopic}
                    </MenuButton>
                    {categories &&
                        <MenuList backgroundColor="#00a0e9">
                            <CMenuItem key="所有主題" onClick={() => {
                                handler("所有主題")
                            }}>
                                所有主題
                            </CMenuItem>
                            {categories.map(c => <CMenuItem key={c} onClick={() => {
                                handler(c)
                            }}>
                                {c}
                            </CMenuItem>)}
                            <MenuDivider/>
                            <CMenuItem backgroundColor="blue"
                                       onClick={() => {
                                           handler("no-show-filter")
                                       }}
                            >{allTopic ? "隱藏未發佈過的主題" : "顯示未發佈過的主題"}</CMenuItem>
                        </MenuList>
                    }
                </Menu>
            </Flex>
        </>
    )
}


function NavBar(props: { data: UIData, functionSet: FunctionSet }) {
    const {data, functionSet} = props;

    return (
        <Box>
            <Flex className="nav" alignItems="center" position="fixed" top="0px" width="100vw">
                <Box ml="16px" mr="16px">ITHome 鐵人觀察家</Box>
                <TopicFilter functionSet={functionSet} categories={data?.categories}/>
            </Flex>

            {/* empty nav for top padding */}
            <Flex className="nav"/>
        </Box>
    )
}


function AppV2() {

    const [data, setData] = useState<UIData | null>();
    const [cookies, setCookie, removeCookie] = useCookies(['selectedTopic']);
    const [allTopic, setAllTopic] = useState(false);
    const [selectedTopic, setSelectedTopic] = useState(typeof cookies.selectedTopic === "undefined" ? "所有主題" : cookies.selectedTopic);


    const functionSet = {
        allTopic,
        setAllTopic,
        selectedTopic,
        setSelectedTopic: (t: string): void => {
            setCookie("selectedTopic", t);
            setSelectedTopic(t);
        }
    }


    useEffect(() => {
        const load = async () => {
            const response: UIData = await fetchData();
            setData(response);
        };
        load();
    }, []);


    return (
        <ChakraProvider>
            <Box>
                <NavBar data={data}
                        functionSet={functionSet}/>
                {
                    jcconf_data && jcconf_data.categories.map(c =>
                        <Category key={c} category={c} data={jcconf_data} selectedTopic="社群推廣區" allTopic={allTopic}/>)
                }
                {
                    data && data.categories.map(c => <Category key={c}
                                                               category={c} data={data}
                                                               selectedTopic={selectedTopic}
                                                               allTopic={allTopic}
                    />)
                }
                {
                    extra_data && extra_data.categories.map(c =>
                        <Category key={c} category={c} data={extra_data} selectedTopic="所有主題" allTopic={allTopic}/>)
                }
            </Box>
        </ChakraProvider>
    )
}


export default AppV2;
