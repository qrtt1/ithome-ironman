import React, { useEffect, useState } from 'react';
import './App.css';

interface TopicProps {
  category: string;
  title: string;
  url: string;
  author: string;
  profileUrl: string;
  view: Number;
}

const Topic = (props: TopicProps) => {
  const Item = (props: { category: string; data?: any; children?: any }) => {
    return (
      <div className={props.category}>
        {props.children ? props.children : props.data}
      </div>
    );
  };
  return (
    <div className='TopicContainer' key={props.url}>
      <Item category='TopicView' data={props.view} />
      <Item category='TopicCategory' data={props.category} />
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
  useEffect(() => {
    const load = async () => {
      const response = await (await fetch('data.json')).text();
      const topics = JSON.parse(response);
      const categories = {};
      topics.map((x: TopicProps) => {
        categories[x.category] = 1;
      });
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
      <div className='Header'>ITHOME 鐵人賽觀賽看版</div>
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
