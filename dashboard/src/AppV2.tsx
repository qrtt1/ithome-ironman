import {Badge, Box, ChakraProvider, Flex, Spacer, Tooltip} from '@chakra-ui/react'
import "./AppV2.css"
import {useEffect, useState} from "react";
import {maxBy} from "lodash";
import {ThemeTypings} from "@chakra-ui/styled-system";
import moment from "moment";

import {ReactComponent as ListAllIcon} from "@vscode/codicons/src/icons/layers-active.svg";
import {ReactComponent as ListPublishedIcon} from "@vscode/codicons/src/icons/layers-dot.svg";
import extra_data from "./extra.json"

async function fetchData() {
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
}

function Topic(props: { topic: TopicEntry }) {
    const {topic} = props;

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
        FAILED: {name: "中斷", color: "blackAlpha"},
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
            <Flex>
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
                <Badge className="tag" colorScheme="gray"> {topic.author}</Badge>
            </Flex>
        </Flex>

    )
}

function Category(props: { category: string, data: UIData, allTopic: boolean }) {
    const {category, data, allTopic} = props;
    return (
        <Flex className="category" direction="column">
            <Flex mb="15px">{category}</Flex>
            {
                data.topics[category].map(t => {
                    if (allTopic) {
                        return <Topic key={t.url} topic={t}/>
                    }

                    if (t.articles.length > 0) {
                        return <Topic key={t.url} topic={t}/>
                    }

                })
            }
        </Flex>
    )
}

function TopicFilter(props: { functionSet: FunctionSet }) {
    const {allTopic, setAllTopic} = props.functionSet
    return (
        <Flex ml="16px" mr="16px" fontSize="10pt" alignItems="center">
            <a onClick={(e) => {
                console.log("xd");
                setAllTopic(!allTopic);
            }} style={{display: "flex", cursor: "pointer"}}>
                {allTopic ? <ListAllIcon/> : <ListPublishedIcon/>}
                {allTopic && <Box ml="5px">顯示所有主題(包含未發表過的主題)</Box>}
                {!allTopic && <Box ml="5px">顯示參賽中發表主題</Box>}
            </a>
        </Flex>
    )
}


function NavBar(props: { data: UIData, functionSet: FunctionSet }) {
    const {data, functionSet} = props;

    return (
        <Box>
            <Flex className="nav" alignItems="center" position="fixed" top="0px" width="100vw">
                <Box ml="16px" mr="16px">ITHome 鐵人賽</Box>
                <TopicFilter functionSet={functionSet}/>
            </Flex>

            {/* empty nav for top padding */}
            <Flex className="nav"/>
        </Box>
    )
}


function AppV2() {

    const [data, setData] = useState<UIData | null>();

    const [allTopic, setAllTopic] = useState(false);

    const functionSet = {allTopic, setAllTopic}

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
                    data && data.categories.map(c => <Category key={c} category={c} data={data} allTopic={allTopic}/>)
                }
                {
                    extra_data && extra_data.categories.map(c =>
                        <Category key={c} category={c} data={extra_data} allTopic={true}/>)
                }
            </Box>
        </ChakraProvider>
    )
}


export default AppV2;