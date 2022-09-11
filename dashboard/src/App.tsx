import React, {useEffect, useState} from 'react';
import './App.css';
import {Link} from "react-router-dom";

interface TopicProps {
    category: string;
    title: string;
    url: string;
    author: string;
    profileUrl: string;
    view: number;
    lastUpdated: number;
}

const dataSource = 'data.json';

const Topic = (props: TopicProps) => {
    const userIsGone = props.title && !props.author;
    if (userIsGone) {
        return <></>;
    }
    const Item = (props: { category: string; data?: any; children?: any }) => {
        return (
            <div className={props.category}>
                {props.children ? props.children : props.data}
            </div>
        );
    };
    return (
        <div className='TopicContainer' key={props.url}>
            <Item category='TopicView' data={props.view}/>
            <Item category='TopicCategory' data={props.category}/>
            <Item category='TopicAuthor'>
                <a href={props.profileUrl} target='_blank' rel='noreferrer'>
                    {props.author}
                </a>
            </Item>
            <Item category='TopicTitle'>
                <a href={props.url} target='_blank' rel='noreferrer'>
                    {props.title}
                </a>
            </Item>
        </div>
    );
};

function App() {
    const [topics, setTopics] = useState([]);
    const [categories, setCategories] = useState([]);
    const [updated, setUpdated] = useState('');

    useEffect(() => {
        let maxTimestamp: number = 0;
        const load = async () => {
            const response = await (
                await fetch(dataSource, {cache: 'no-store'})
            ).text();
            const topics = JSON.parse(response);
            const categories = {};
            topics.map((x: TopicProps) => {
                if (x.lastUpdated > maxTimestamp) {
                    maxTimestamp = x.lastUpdated;
                }
                categories[x.category] = 1;
                return x.category;
            });
            setUpdated(`${new Date(maxTimestamp)}`);
            setCategories(Object.keys(categories).sort());
            topics.sort((a: TopicProps, b: TopicProps) => {
                if (a.view === b.view) {
                    return 0;
                }
                return a.view > b.view ? -1 : 1;
            });
            setTopics(topics);
        };
        load();
    }, []);
    return (
        <>
            <div className='Header' style={{display: "flex", alignItems: "center", alignContent: "space-between"}}>
                <div>
                    ITHOME 鐵人賽觀賽看版{' '}
                </div>
                <div style={{fontSize: 8, color: 'white', marginLeft: 24}}>
                    {updated}
                </div>

                <a href="https://ithome-ironman-watcher.s3.ap-northeast-1.amazonaws.com/2022v2/index.html"
                   style={{fontSize: 9, color: 'yellow', marginLeft: 24}}>v2
                    dashboard</a>

            </div>
            <div className='App'>
                <div>
                    {categories.map((c) => (
                        <div>
                            <h1>{c}</h1>
                            {topics
                                .filter((x: TopicProps) => {
                                    return x.category === c;
                                })
                                .map((data: TopicProps) => (
                                    <Topic {...data} />
                                ))}
                        </div>
                    ))}
                </div>
            </div>
        </>
    );
}

export default App;
