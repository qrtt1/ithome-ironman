import React, { useEffect, useState } from "react";
import "./App.css";

interface TopicProps {
  category: string;
  title: string;
  url: string;
  author: string;
  profileUrl: string;
  view: Number;
}

const Topic = (props: TopicProps) => {
  const [hoverStatus, setHoverStatus] = useState(false);

  const Item = (props: { category: string; data?: any; children?: any }) => {
    const hover = (event) => {
      if (event.type === "mouseenter") {
        setHoverStatus(true);
      } else {
        setHoverStatus(false);
      }
    };

    let cssClass: string;
    if (hoverStatus) {
      cssClass = `${props.category} HighLight`;
    } else {
      cssClass = props.category;
    }

    return (
      <div className={cssClass} onMouseEnter={hover} onMouseLeave={hover}>
        {props.children ? props.children : props.data}
      </div>
    );
  };
  return (
    <div className={" TopicContainer"} key={props.url}>
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
  useEffect(() => {
    const load = async () => {
      const response = await (await fetch("data.json")).text();
      const topics = JSON.parse(response);
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
      <div className='Header'>ITHOME 鐵人賽</div>
      <div className='App'>
        <div>
          {topics.map((data: TopicProps) => (
            <Topic {...data} key={data.title} />
          ))}
        </div>
      </div>
    </>
  );
}

export default App;
