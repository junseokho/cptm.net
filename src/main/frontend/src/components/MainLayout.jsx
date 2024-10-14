/**
 * @author 손의현(SONY_STRING)
 * @description 화면 레이아웃입니다
 *
 */

import '../assets/css/main_layout.scss'
import {Lobby, MenuRight} from "../components/Lobby.jsx";

function Layout() {
    // todo menu:n is test
    return (
        <div className="empty">
            <div className="header">
                <div className="menu">
                    <p>menu1</p>
                </div>
                <div className="menu">
                    <p>menu2</p>
                </div>
                <div className="menu">
                    <p>menu3</p>
                </div>
            </div>
            <div className="body">
                <div className="body-left">
                    <div className="menu">
                        <p>menu1</p>
                    </div>
                    <div className="menu">
                        <p>menu2</p>
                    </div>
                    <div className="menu">
                        <p>menu3</p>
                    </div>
                    <div className="menu">
                        <p>menu4</p>
                    </div>
                    <div className="menu">
                        <p>menu5</p>
                    </div>
                    <div className="menu">
                        <p>menu6</p>
                    </div>
                    <div className="menu">
                        <p>menu7</p>
                    </div>
                </div>
                <div className="body-center">
                    <Lobby/>
                </div>
                <div className="body-right">
                    <MenuRight/>
                </div>
            </div>
        </div>
    )
}

export default Layout;