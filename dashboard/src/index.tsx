import React from 'react';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {createRoot} from "react-dom/client";
import AppV2 from "./AppV2";

const root = createRoot(document.getElementById('root'));

function MyRoute() {
    return <AppV2/>
}

root.render(
    <React.StrictMode>
        <MyRoute/>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
