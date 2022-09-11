import {Badge, Box, Center, ChakraProvider, Flex, Spacer, Tooltip} from '@chakra-ui/react'
import "./AppV2.css"
import {useEffect, useState} from "react";
import {maxBy} from "lodash";
import {ThemeTypings} from "@chakra-ui/styled-system";
import moment from "moment";


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

function Topic(props: { topic: TopicEntry }) {
    const {topic} = props;
    const refData = new Date();

    const latestArticle = maxBy(topic.articles, (o) => {
        return new Date(o.iso8601Published)
    });

    let status: Status = {content: "期待", color: "purple", date: ""};
    if (latestArticle != null) {
        const diffHours = moment(refData).diff(moment(new Date(latestArticle.iso8601Published)), "hours");
        const diffDays = moment(refData).diff(moment(new Date(latestArticle.iso8601Published)), "days");
        if (diffDays > 2) {
            status = {content: "斷賽", color: "blackAlpha", date: latestArticle.iso8601Published}
        }
        if (diffDays <= 2) {
            status = {content: "安全", color: "blue", date: latestArticle.iso8601Published}
        }
        if (diffHours <= 12) {
            status = {content: "新的", color: "green", date: latestArticle.iso8601Published}
        }

        console.log(`${topic.title} => ${diffDays} => ${status}`);
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
            <Flex>
                <a href={topic.url}>
                    {topic.title}
                </a>
            </Flex>
            <Spacer/>
            <Flex>
                {latestArticle &&
                    <a href={latestArticle.url} target="_blank">
                        <Badge mr={1} pl={5} pr={5}
                               backgroundColor="gray.400" color="white">
                            {latestArticle && latestArticle.title}</Badge>
                    </a>

                }
                <Badge pl={5} pr={5} colorScheme="gray"> {topic.author}</Badge>
            </Flex>
        </Flex>

    )
}

function Category(props: { category: string, data: UIData }) {
    const {category, data} = props;
    return (
        <Flex className="category" direction="column">
            <Flex mb="15px">{category}</Flex>
            {
                data.topics[category].map(t => <Topic key={t.url} topic={t}/>)
            }
        </Flex>
    )
}


function AppV2() {

    const [data, setData] = useState<UIData | null>();

    useEffect(() => {
        const load = async () => {
            const data = await fetchData();
            setData(data);
        };
        load();
    }, []);

    return (
        <ChakraProvider>
            <Box>
                <Flex className="nav" alignItems="center">
                    <Box ml="16px">ITHome 鐵人賽</Box>
                </Flex>
                {
                    data && data.categories.map(c => <Category key={c} category={c} data={data}/>)
                }
            </Box>
        </ChakraProvider>
    )
}


export default AppV2;